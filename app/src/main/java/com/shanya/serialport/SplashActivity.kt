package com.shanya.serialport

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.shanya.serialport.update.DownloadUtil
import com.shanya.serialport.update.Update
import com.shanya.serialport.update.VersionInfo
import com.shanya.serialport.update.VolleySingleton
import java.util.*

const val UPDATE_CODE_YES = 0x986
const val UPDATE_CODE_NO = 0x985
class SplashActivity : AppCompatActivity() {

    private lateinit var infoViewModel: InfoViewModel




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        infoViewModel = ViewModelProvider(this).get(InfoViewModel::class.java)
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

            val stringRequest = StringRequest(
                Request.Method.GET,
                "https://raw.githubusercontent.com/Shanyaliux/SerialPort/master/update/update.json",
                Response.Listener {
                    val versionCode = Gson().fromJson(it,VersionInfo::class.java).versionCode
                    val updateContent = Gson().fromJson(it,VersionInfo::class.java).updateContent
                    val fileName = Gson().fromJson(it,VersionInfo::class.java).fileName
                    if (BuildConfig.VERSION_CODE < versionCode) {
                        val builder: AlertDialog.Builder =
                            AlertDialog.Builder(this)
                                .setTitle("发现新版本")
                                .setMessage(updateContent)
                                .setPositiveButton("立即下载",
                                    DialogInterface.OnClickListener { dialog, which ->
                                        val downloadUtil = DownloadUtil(
                                            this,
                                            "https://github.com/Shanyaliux/AppUpdate/releases/download/test1/app-debug.apk",
                                            fileName
                                        )
                                        val intent = Intent(this@SplashActivity, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    })
                                .setNegativeButton("以后再说",
                                    DialogInterface.OnClickListener { dialog, which ->
                                        val intent = Intent(this@SplashActivity, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    })
                        builder.show()

                    }else{
                        val intent = Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                },
                Response.ErrorListener {
                    val handler = Handler()
                    val msg = Message.obtain()
                    msg.what = UPDATE_CODE_NO
                    handler.sendMessage(msg)
                    Log.d("VolleyErrorListener",it.toString())
                })
            VolleySingleton.getInstance(application).requestQueue.add(stringRequest)

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
                        val stringRequest = StringRequest(
                            Request.Method.GET,
                            "https://raw.githubusercontent.com/Shanyaliux/SerialPort/master/update/update.json",
                            Response.Listener {
                                val versionCode = Gson().fromJson(it,VersionInfo::class.java).versionCode
                                val updateContent = Gson().fromJson(it,VersionInfo::class.java).updateContent
                                val fileName = Gson().fromJson(it,VersionInfo::class.java).fileName
                                if (BuildConfig.VERSION_CODE < versionCode) {
                                    val builder: AlertDialog.Builder =
                                        AlertDialog.Builder(this)
                                            .setTitle("发现新版本")
                                            .setMessage(updateContent)
                                            .setPositiveButton("立即下载",
                                                DialogInterface.OnClickListener { dialog, which ->
                                                    val downloadUtil = DownloadUtil(
                                                        this,
                                                        "https://github.com/Shanyaliux/AppUpdate/releases/download/test1/app-debug.apk",
                                                        fileName
                                                    )
                                                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                                                    startActivity(intent)
                                                    finish()
                                                })
                                            .setNegativeButton("以后再说",
                                                DialogInterface.OnClickListener { dialog, which ->
                                                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                                                    startActivity(intent)
                                                    finish()
                                                })
                                    builder.show()

                                }else{
                                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            },
                            Response.ErrorListener {
                                val handler = Handler()
                                val msg = Message.obtain()
                                msg.what = UPDATE_CODE_NO
                                handler.sendMessage(msg)
                                Log.d("VolleyErrorListener",it.toString())
                            })
                        VolleySingleton.getInstance(application).requestQueue.add(stringRequest)
//

                    }
                }
            }
        }
    }

}
