package com.harishsk.saveblood.ui

import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.harishsk.saveblood.R
import java.util.*

class SplashScreen : AppCompatActivity() {

    private var bdelay = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        if (Build.VERSION.SDK_INT >= 23) checkPermissions()
        var delay = 1000
        if (bdelay) delay = 3000
        when {
            !bdelay -> Handler(mainLooper).postDelayed(
                { main() },
                delay.toLong()
            )
            else -> Handler(mainLooper).postDelayed(
                { main() }, 6000
            )
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun checkPermissions() {
        val unGrantedPermissions = requiredPermissionsStillNeeded()
        if (unGrantedPermissions.isNotEmpty()) {
            requestPermissions(
                unGrantedPermissions,
                PERMISSIONS_REQUEST
            )
            bdelay = true
        }
    }

    private val requiredPermissions: Array<String?>
        get() {
            var permissions: Array<String?>? = null
            try {
                permissions = packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_PERMISSIONS
                ).requestedPermissions
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return permissions?.clone() ?: arrayOfNulls(0)
        }

    @TargetApi(23)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST) checkPermissions()
    }

    @TargetApi(23)
    private fun requiredPermissionsStillNeeded(): Array<String> {
        val permissions: MutableSet<String> =
            HashSet()
        Collections.addAll<String>(permissions, *requiredPermissions)
        val i = permissions.iterator()
        while (i.hasNext()) {
            val permission = i.next()
            when (PackageManager.PERMISSION_GRANTED) {
                checkSelfPermission(permission) -> {
                    Log.d(
                        TAG,
                        getString(R.string.Permission) + permission + getString(
                            R.string.already_granted
                        )
                    )
                    i.remove()
                }
                else -> Log.d(
                    TAG,
                    getString(R.string.Permission) + permission + getString(
                        R.string.not_granted
                    )
                )
            }
        }
        return permissions.toTypedArray()
    }

    private fun main() {
        startActivity(Intent(this@SplashScreen, PhoneAuthActivity::class.java))
        finish()
    }

    companion object {
        private val TAG =
            SplashScreen::class.java.simpleName
        private const val PERMISSIONS_REQUEST = 1234
    }
}