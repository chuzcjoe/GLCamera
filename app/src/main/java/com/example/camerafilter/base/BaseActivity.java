package com.example.camerafilter.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

/**
 * Created by joe.chu on 1/21/24
 *
 * @author joe.chu@bytedance.com
 */
public class BaseActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1109;
    private String permissionDes;
    private Callback callback;

    @SuppressLint("MissingPermission")
    private ActivityResultLauncher<String[]> requestMultiplePermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                // Check if all permissions are granted
                boolean allPermissionsGranted = true;
                for (Boolean isGranted : permissions.values()) {
                    if (!isGranted) {
                        allPermissionsGranted = false;
                        break;
                    }
                }
                if (allPermissionsGranted) {
                    Toast.makeText(this, "all access granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "can't open camera", Toast.LENGTH_SHORT).show();
                }
            });

    public void requestPermission(String permissionDes, Callback callback, @NonNull String... permissions){
        this.permissionDes = permissionDes;
        this.callback = callback;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            requestMultiplePermissionsLauncher.launch(permissions);
        }
    }

    public boolean checkPermissions(@NonNull String... permissions){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return checkselfPermissions(permissions);
        }
        return true;
    }

    public boolean checkselfPermissions(@NonNull String... permissions){
        boolean granted = true;
        for (String permission : permissions){
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED){
                granted = false;
                break;
            }
        }
        return granted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean granted = true;
        for(int i = 0; i < grantResults.length; i++){
            if (grantResults[i] == PackageManager.PERMISSION_DENIED){
                granted = false;
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])){
                    showPromptDialog();
                }else {
                    if (callback != null) callback.failed();
                }
                break;
            }
        }
        if (granted){
            if (callback != null) callback.success();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void showPromptDialog(){
        new AlertDialog
                .Builder(this)
                .setTitle("权限申请")
                .setMessage(permissionDes)
                .setCancelable(false)
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        toAppSetting();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (callback != null) callback.failed();
                    }
                }).show();
    }

    public void toAppSetting(){
        Intent settingIntent = null;
        if (Build.VERSION.SDK_INT >= 9){
            settingIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            settingIntent.setData(Uri.fromParts("package", getPackageName(), null));
            settingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }else {
            settingIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            settingIntent.setAction(Intent.ACTION_VIEW);
            settingIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            settingIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
            settingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(settingIntent);
    }

    public interface Callback{
        void success();
        void failed();
    }
}
