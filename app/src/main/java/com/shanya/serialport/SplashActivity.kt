package com.shanya.serialport

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.ArrayList


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET)
        val mPermissionList = ArrayList<String>()
        for (p in permissions){
            if (ContextCompat.checkSelfPermission(this,p) != PackageManager.PERMISSION_GRANTED){
                mPermissionList.add(p)
            }
        }
        if (mPermissionList.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION)
        }else{
            Toast.makeText(this,"已获取所需权限",Toast.LENGTH_SHORT).show()
            val handler = Handler()
            handler.postDelayed(Runnable {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 1500)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_PERMISSION -> {
                for (element in grantResults){
                    if (element != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this,"有权限未获取，可能会影响使用", Toast.LENGTH_SHORT).show()
                    }else{
                        val handler = Handler()
                        handler.postDelayed(Runnable {
                            val intent = Intent(this@SplashActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }, 1500)
                    }
                }
            }
        }
    }

}
