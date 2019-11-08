package com.tas.beaconzz.Floating;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.tas.beaconzz.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

//카메라를 FloatingView하기 위한 곳
public class FloatingViewCameraService extends Service {
    private WindowManager mWindowManager;
    private View floatingView;
    boolean setBackCamera = true;
    boolean flagCanTakePicture = true;
    private Camera mCamera;
    int zoomCounter = 0;
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            mCamera.startPreview();
            if (pictureFile == null){
                System.out.println("사진 파일 생성 실패");
                flagCanTakePicture = true;
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://"+ pictureFile)));
                flagCanTakePicture = true;
            } catch (Exception e){
                e.printStackTrace();
                System.out.println("사진 저장 실패");
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_camera, null);

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 100;

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(floatingView, params);

        prepareCameraView(setBackCamera);

        Button btnMakePhoto = floatingView.findViewById(R.id.btnMakePhoto);
        btnMakePhoto.setOnClickListener(
                v -> {
                    if (flagCanTakePicture) {
                        flagCanTakePicture = false;
                        mCamera.takePicture(null, null, mPicture);
                        Toast.makeText(this,"사진 촬영 성공!",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        ImageView btnClose = floatingView.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(
                v -> {
                    releaseCamera();
                    stopSelf();
                }
        );
        Button btnZoomCamera = floatingView.findViewById(R.id.btnZoomInCamera);
        btnZoomCamera.setOnClickListener(
                v -> {
                    Camera.Parameters cameraParameters = mCamera.getParameters();
                    if(zoomCounter<cameraParameters.getMaxZoom()){
                        zoomCounter += 3;
                        setCameraParameters(mCamera,zoomCounter);
                    }
                }
        );
        Button btnZoomOutCamera = floatingView.findViewById(R.id.btnZoomOutCamera);
        btnZoomOutCamera.setOnClickListener(
                v -> {
                    if(zoomCounter>0){
                        zoomCounter -= 3;
                        setCameraParameters(mCamera,zoomCounter);
                    }
                }
        );


        floatingView.findViewById(R.id.linearLayout).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        initialX = params.x;
                        initialY = params.y;

                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);

                        if (Xdiff < 10 && Ydiff < 10) {
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(floatingView, params);
                        return true;
                }
                return false;
            }
        });
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }
    }

    private void prepareCameraView(boolean setBackCamera){
        mCamera = getCameraInstance(setBackCamera);
        setCameraParameters(mCamera, 0);
        CameraPreview mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = floatingView.findViewById(R.id.camera_preview);
        preview.removeAllViews();
        preview.addView(mPreview);
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.autoFocus (new Camera.AutoFocusCallback() {
                    public void onAutoFocus(boolean success, Camera camera) {
                        if(success){
                            Toast.makeText(getApplicationContext(),"Auto Focus 성공",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Auto Focus 실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void setCameraParameters(Camera camera, int zoom) {
        Camera.Parameters cameraParameters = camera.getParameters();
        cameraParameters.setZoom(zoom);
        ArrayList<Integer> sizes = new ArrayList<>();
        for (int i = 0; i < cameraParameters.getSupportedPictureSizes().size(); i++) {
            sizes.add(cameraParameters.getSupportedPictureSizes().get(i).width
                    * cameraParameters.getSupportedPictureSizes().get(i).height);
        }
        int max = 0;
        for (int i = 0; i < sizes.size(); i++) {
            if (sizes.get(i) > sizes.get(i)) {
                max = i;
            }
        }
        cameraParameters.setPictureSize(cameraParameters.getSupportedPictureSizes().get(max).width,
                cameraParameters.getSupportedPictureSizes().get(max).height);
        camera.setParameters(cameraParameters);
    }


    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){


        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");


        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "폴더 생성 실패");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(boolean setBackCamera){
        Camera c = null;
        try {
            c = Camera.open(setBackCamera ? 0 : 1);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return c;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingView != null) mWindowManager.removeView(floatingView);
    }
}