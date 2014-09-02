package com.jasonrobinson.zenithfinder.ui;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.WindowManager;
import android.widget.Toast;

import com.jasonrobinson.zenithfinder.R;

public class CameraActivity extends FragmentActivity {

    private static final String TAG_CAMERA = "camera";
    private static final String TAG_SENSOR = "sensor";

    Camera mCamera;

    CameraFragment mCameraFragment;

    private static Camera getCameraInstance() throws Exception {
        try {
            return Camera.open();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            initCamera();
            if (mCamera == null) {
                return;
            }

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            mCameraFragment = CameraFragment.newInstance();
            ft.add(android.R.id.content, mCameraFragment, TAG_CAMERA);

            double[] viewAngles = getCameraViewAngle();
            ft.add(android.R.id.content, SensorFragment.newInstance(viewAngles[0], viewAngles[1]), TAG_SENSOR);

            ft.commit();
        } else {
            mCameraFragment = (CameraFragment) getSupportFragmentManager().findFragmentByTag(TAG_CAMERA);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            initCamera();
        }

        if (mCamera != null) {
            mCameraFragment.setCamera(mCamera);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private double[] getCameraViewAngle() {
        Camera.Parameters p = mCamera.getParameters();
        double thetaH = Math.toRadians(p.getHorizontalViewAngle());
        double thetaV = Math.toRadians(p.getVerticalViewAngle());

        return new double[]{thetaH, thetaV};
    }

    private void initCamera() {
        try {
            mCamera = getCameraInstance();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.camera_error), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
}
