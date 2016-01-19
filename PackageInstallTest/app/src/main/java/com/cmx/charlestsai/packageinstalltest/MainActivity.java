package com.cmx.charlestsai.packageinstalltest;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "PackageInstallTest";

    private static int REQUEST_INSTALL_APK = 0x1000;

    private static String TARGET_APK_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TARGET_APK_NAME = getResources().getText(R.string.target_apk_name).toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_INSTALL_APK) {
            // delete the temp file while installer is returned.
            Log.w(TAG, "Deleting apk file for temp. Result code=" + resultCode);
            File targetFile = new File(getFilesDir(), TARGET_APK_NAME);
            targetFile.delete();

            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Package installation completed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void onClickButton(View view) {
        // install the package
        Log.d(TAG, "Start installing package!");

        Intent installIntent = null;

        try {
            installIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            installIntent.setData(resourceToFileUri());
            installIntent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            installIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        } catch (Exception e) {
            Log.w(TAG, "Exception happens!!!");
        }

        startActivityForResult(installIntent, REQUEST_INSTALL_APK);
    }

    private Uri resourceToFileUri() {
        File targetFile = null;

        try {
            AssetFileDescriptor apkFd = getResources().getAssets().openFd(TARGET_APK_NAME);

            targetFile = new File(getFilesDir(), TARGET_APK_NAME);

            FileOutputStream outputStream = new FileOutputStream(targetFile);
            FileInputStream inputStream = apkFd.createInputStream();
            //inputStream.reset();

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            targetFile.setReadable(true, false);

        } catch (Exception e) {
            Log.w(TAG, "Exception happens while converting URI!");
            e.printStackTrace();
        }

        Uri result = Uri.fromFile(targetFile);

        return result;
    }
}
