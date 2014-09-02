package com.jasonrobinson.zenithfinder.ui;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jasonrobinson.zenithfinder.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CameraFragment extends Fragment {

    @InjectView(R.id.cameraPreview)
    CameraPreview mCameraPreview;

    Camera mCamera;

    boolean mViewCreated;

    public static CameraFragment newInstance() {
        return new CameraFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        mViewCreated = true;

        if (mCamera != null) {
            mCameraPreview.setCamera(mCamera);
        }
    }

    @Override
    public void onDestroyView() {
        mViewCreated = false;
        super.onDestroyView();
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
        if (mViewCreated && mCamera != null) {
            mCameraPreview.setCamera(mCamera);
        }
    }
}
