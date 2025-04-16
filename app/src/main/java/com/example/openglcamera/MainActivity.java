package com.example.openglcamera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.openglcamera.camera.AutoFitSurfaceView;
import com.example.openglcamera.camera.Camera2Controller;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    
    private AutoFitSurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera2Controller mCamera2Controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize camera controller
        mCamera2Controller = new Camera2Controller(this);
        
        // Initialize SurfaceView
        mSurfaceView = findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

        mCamera2Controller.setSurfaceView(mSurfaceView);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mCamera2Controller.startBackgroundThread();
    }
    
    @Override
    protected void onPause() {
        closeCamera();
        super.onPause();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCamera2Controller.stopBackgroundThread();
    }

    private void openCamera() {
        if (mSurfaceView == null) {
            mSurfaceView.post(() -> {
                openCamera();
            });
        }
    }
    
    private void closeCamera() {
        mCamera2Controller.closeCamera();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            return;
        }

        Log.d(TAG, "Opening camera with permission granted");
        openCamera();
    }
    
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged: " + width + ", " + height);
    }
    
    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        closeCamera();
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
        // add more permission checks
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }
        }
    }
}