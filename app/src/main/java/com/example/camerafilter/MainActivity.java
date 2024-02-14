package com.example.camerafilter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.camerafilter.base.BaseActivity;
import com.example.camerafilter.fragments.GLPreviewFragment;
import com.example.camerafilter.fragments.PhotoFragment;
import com.example.camerafilter.fragments.PreviewFragment;
import com.example.camerafilter.utils.CameraUtils;
import com.example.camerafilter.utils.GLUtil;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    Button btnPreview, btnCapture, btnGLPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CameraUtils.init(this);
        GLUtil.init(this);

        btnPreview = findViewById(R.id.btnPreview);
        btnPreview.setOnClickListener(this);

        btnCapture = findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(this);

        btnGLPreview = findViewById(R.id.btnGLPreview);
        btnGLPreview.setOnClickListener(this);

        requestPermission("请给予相机、存储权限，以便app正常工作", null,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES});
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnPreview) {
            transactFragment(new PreviewFragment());
        } else if (v.getId() == R.id.btnCapture) {
            transactFragment(new PhotoFragment());
        } else if (v.getId() == R.id.btnGLPreview) {
            transactFragment(new GLPreviewFragment());
        }
    }

    private void transactFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_camera, fragment)
                .commit();
    }
}