package com.harishsk.saveblood;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SplashScreen extends AppCompatActivity {

    private static final String TAG = SplashScreen.class.getSimpleName();

    private static final int PERMISSIONS_REQUEST = 1234;

    private Boolean bdelay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (Build.VERSION.SDK_INT >= 23) checkPermissions();

        int delay = 1000;
        if (bdelay) delay = 3000;
        if (!bdelay)
            new Handler().postDelayed(this::main, delay);
        else
            new Handler().postDelayed(this::main, 6000);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        String[] ungrantedPermissions = requiredPermissionsStillNeeded();
        if (ungrantedPermissions.length != 0) {
            requestPermissions(ungrantedPermissions, PERMISSIONS_REQUEST);
            bdelay = true;
        }
    }

    public String[] getRequiredPermissions() {
        String[] permissions = null;
        try {
            permissions = getPackageManager().getPackageInfo(getPackageName(),
                    PackageManager.GET_PERMISSIONS).requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (permissions == null) return new String[0];
        else return permissions.clone();
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) checkPermissions();
    }

    @TargetApi(23)
    private String[] requiredPermissionsStillNeeded() {

        Set<String> permissions = new HashSet<>();
        Collections.addAll(permissions, getRequiredPermissions());
        for (Iterator<String> i = permissions.iterator(); i.hasNext(); ) {
            String permission = i.next();
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, getString(R.string.Permission) + permission + getString(R.string.already_granted));
                i.remove();
            } else
                Log.d(TAG, getString(R.string.Permission) + permission + getString(R.string.not_granted));
        }
        return permissions.toArray(new String[permissions.size()]);
    }

    public void main() {
        startActivity(new Intent(SplashScreen.this, PhoneAuthActivity.class));
        finish();
    }
}
