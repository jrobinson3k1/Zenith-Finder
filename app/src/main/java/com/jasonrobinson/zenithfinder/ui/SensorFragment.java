package com.jasonrobinson.zenithfinder.ui;

import android.annotation.TargetApi;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.jasonrobinson.zenithfinder.R;
import com.jasonrobinson.zenithfinder.module.GraphHolder;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SensorFragment extends Fragment implements ViewTreeObserver.OnGlobalLayoutListener, SensorEventListener {

    private static final String EXTRA_HORIZONTAL_VIEW_ANGLE = "horizontal_view_angle";
    private static final String EXTRA_VERTICAL_VIEW_ANGLE = "vertical_view_angle";

    private static final float[] ZENITH = new float[]{0, (float) Math.PI}; // pitch, roll

    @InjectView(R.id.zenith_TextView)
    TextView mZenithTextView;

    @Inject
    SensorManager mSensorManager;

    Sensor mAccelerometerSensor;
    Sensor mMagneticFieldSensor;

    float[] mGravityMatrix;
    float[] mGeomagneticMatrix;

    double mHorizontalViewAngle;
    double mVerticalViewAngle;

    int mViewWidth;
    int mViewHeight;

    public static SensorFragment newInstance(double horizontalViewAngle, double verticalViewAngle) {
        SensorFragment fragment = new SensorFragment();

        Bundle args = new Bundle();
        args.putDouble(EXTRA_HORIZONTAL_VIEW_ANGLE, horizontalViewAngle);
        args.putDouble(EXTRA_VERTICAL_VIEW_ANGLE, verticalViewAngle);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GraphHolder.getInstance().inject(this);

        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticFieldSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mHorizontalViewAngle = getArguments().getDouble(EXTRA_HORIZONTAL_VIEW_ANGLE);
        mVerticalViewAngle = getArguments().getDouble(EXTRA_VERTICAL_VIEW_ANGLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sensor, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        view.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagneticFieldSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onGlobalLayout() {
        if (getView() != null) {
            mViewWidth = getView().getWidth();
            mViewHeight = getView().getHeight();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravityMatrix = event.values.clone();
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagneticMatrix = event.values.clone();
        }

        if (mGravityMatrix != null && mGeomagneticMatrix != null && isViewSizeInitialized()) {
            float[] rMatrix = new float[16];

            if (SensorManager.getRotationMatrix(rMatrix, null, mGravityMatrix, mGeomagneticMatrix)) {
                float[] orientation = new float[3];

                SensorManager.getOrientation(rMatrix, orientation);
                float pitch = orientation[1];
                float roll = orientation[2];

                if (canSeeZenith(pitch, roll)) {
                    showCircle();

                    float[] position = getZenithPosition(pitch, roll);
                    moveCircle(position[0], position[1]);
                } else {
                    hideCircle();
                }
            }
        }
    }

    private float[] getZenithPosition(float pitch, float roll) {
        float x = (float) (Math.abs(Math.abs(pitch) - Math.abs(ZENITH[0])) * (mViewWidth / mHorizontalViewAngle));
        float y = (float) (Math.abs(Math.abs(roll) - Math.abs(ZENITH[1])) * (mViewHeight / mVerticalViewAngle));

        if (pitch < 0) {
            x = -x;
        }

        if (roll < 0) {
            y = -y;
        }

        return new float[]{x, y};
    }

    private void moveCircle(float x, float y) {
        mZenithTextView.setX(x);
        mZenithTextView.setY(y);
    }

    private void hideCircle() {
        mZenithTextView.setVisibility(View.INVISIBLE);
    }

    private void showCircle() {
        mZenithTextView.setVisibility(View.VISIBLE);
    }

    private boolean isViewSizeInitialized() {
        return mViewWidth > 0 && mViewHeight > 0;
    }

    private boolean canSeeZenith(float pitch, float roll) {
        boolean h = Math.abs(Math.abs(ZENITH[0]) - Math.abs(pitch)) <= mHorizontalViewAngle / 2;
        boolean v = Math.abs(Math.abs(ZENITH[1]) - Math.abs(roll)) <= mVerticalViewAngle / 2;

        return h && v;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // no-op
    }
}
