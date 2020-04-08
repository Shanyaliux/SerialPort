package com.shanya.serialport

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.shanya.serialport.update.DownloadUtil
import com.shanya.serialport.update.Update
import com.shanya.serialport.update.VersionInfo
import java.util.*

const val UPDATE_CODE = 1
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

            Update.checkUpdate(this)
//            val handler = Handler()
//            handler.postDelayed(Runnable {
//                val intent = Intent(this@SplashActivity, MainActivity::class.java)
//                startActivity(intent)
//                finish()
//            }, 1000)
            val handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    super.handleMessage(msg)
                    when(msg.what){
                        UPDATE_CODE -> {
                            val versionInfo: VersionInfo = msg.obj as VersionInfo
                            val builder: AlertDialog.Builder =
                                AlertDialog.Builder(this@SplashActivity)
                                    .setTitle("发现新版本")
                                    .setMessage(versionInfo.updateContent)
                                    .setPositiveButton("立即下载",
                                        DialogInterface.OnClickListener { dialog, which ->
                                            val downloadUtil = DownloadUtil(
                                                this@SplashActivity,
                                                "https://github.com/Shanyaliux/AppUpdate/releases/download/test1/app-debug.apk",
                                                versionInfo.fileName
                                            )
                                        })
                                    .setNegativeButton("以后再说",
                                        DialogInterface.OnClickListener { dialog, which -> })
                            builder.show()
                        }
                    }
                }
            }
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
//                        handler.postDelayed(Runnable {
//                            val intent = Intent(this@SplashActivity, MainActivity::class.java)
//                            startActivity(intent)
//                            finish()
//                        }, 1000)
                        Update.checkUpdate(this)
                    }
                }
            }
        }
    }

}
