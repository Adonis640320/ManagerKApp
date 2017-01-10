package com.sincere.kboss.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by developer on 2016-09-21.
 */
public class CameraPreview1 extends SurfaceView implements SurfaceHolder.Callback {
    private final String TAG = getClass().getSimpleName();
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview1(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            Camera.Parameters parameters = mCamera.getParameters();
            if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set("orientation", "portrait");
                mCamera.setDisplayOrientation(90);
                parameters.setRotation(90);
            } else {
                parameters.set("orientation", "landscape");
                mCamera.setDisplayOrientation(0);
                parameters.setRotation(0);
            }
            mCamera.setParameters(parameters);

            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.e(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // 프리뷰를 회전시키거나 변경시 처리를 여기서 해준다.
        // 프리뷰 변경시에는 먼저 프리뷰를 멈춘다음 변경해야한다.
        if (mHolder.getSurface() == null){
            // 프리뷰가 존재하지 않을때
            return;
        }

        // 우선 멈춘다
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // 프리뷰가 존재조차 하지 않는 경우다
        }

        // 프리뷰 변경, 처리 등을 여기서 해준다.
        Camera.Parameters parameters = mCamera.getParameters();
        Camera.Size size = getBestPreviewSize(width, height);
        parameters.setPreviewSize(size.width, size.height);
        mCamera.setParameters(parameters);
        // 새로 변경된 설정으로 프리뷰를 재생성한다
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.e(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "surfaceDestroyed called!!!");
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }


    private Camera.Size getBestPreviewSize(int width, int height)
    {
        Camera.Size result=null;
        Camera.Parameters p = mCamera.getParameters();
        for (Camera.Size size : p.getSupportedPreviewSizes()) {
            if (size.width<=width && size.height<=height) {
                if (result==null) {
                    result=size;
                } else {
                    int resultArea=result.width*result.height;
                    int newArea=size.width*size.height;

                    if (newArea>resultArea) {
                        result=size;
                    }
                }
            }
        }
        return result;
    }
}
