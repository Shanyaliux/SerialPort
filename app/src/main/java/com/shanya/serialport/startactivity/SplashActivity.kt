package com.shanya.serialport.startactivity

import android.Manifest
import android.animation.AnimatorInflater
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.shanya.serialport.*
import com.shanya.serialport.update.DownloadUtil
import com.shanya.serialport.update.VersionInfo
import com.shanya.serialport.update.VolleySingleton
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*

const val UPDATE_CODE_YES = 0x986
const val UPDATE_CODE_NO = 0x985
const val JSON_URL = "https://raw.githubusercontent.com/Shanyaliux/SerialPort/master/update/update.json"
const val APK_URL = "https://github.com/Shanyaliux/SerialPort/releases/download/V1.1/app-release.apk"
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val animationTag = AnimationUtils.loadAnimation(this,R.anim.slide_from_left)
        animationTag.duration = 500
        val animationLogo = AnimationUtils.loadAnimation(this,R.anim.slide_from_right)
        animationLogo.duration = 500
        textViewMainTag.animation = animationTag
        textViewMainTag.visibility = View.VISIBLE
        imageViewDxLogo.animation = animationLogo
        imageViewDxLogo.visibility = View.VISIBLE

        //权限申请
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
            ActivityCompat.requestPermissions(this, permissions,
                REQUEST_PERMISSION
            )
        }else{//全部权限已申请
            Toast.makeText(this,"已获取所需权限",Toast.LENGTH_SHORT).show()

            val stringRequest = StringRequest(
                Request.Method.GET,
                JSON_URL,
                Response.Listener {
                    val versionCode = Gson().fromJson(it,VersionInfo::class.java).versionCode
                    val updateContent = Gson().fromJson(it,VersionInfo::class.java).updateContent
                    val fileName = Gson().fromJson(it,VersionInfo::class.java).fileName
                    if (BuildConfig.VERSION_CODE < versionCode) {
                        //显示更新对话框
//                        updateDialog(updateContent, fileName)
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
                            JSON_URL,
                            Response.Listener {
                                val versionCode = Gson().fromJson(it,VersionInfo::class.java).versionCode
                                val updateContent = Gson().fromJson(it,VersionInfo::class.java).updateContent
                                val fileName = Gson().fromJson(it,VersionInfo::class.java).fileName
                                if (BuildConfig.VERSION_CODE < versionCode) {

//                                    updateDialog(updateContent, fileName)

                                }else{
                                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            },
                            Response.ErrorListener {
                                val handler = Handler()
                                val msg = Message.obtain()
                                msg.what =
                                    UPDATE_CODE_NO
                                handler.sendMessage(msg)
                                Log.d("VolleyErrorListener",it.toString())
                            })
                        VolleySingleton.getInstance(application).requestQueue.add(stringRequest)
                    }
                }
            }
        }
    }

    private fun updateDialog(updateContent: String, fileName: String) {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this)
                .setTitle("发现新版本")
                .setMessage(updateContent)
                .setPositiveButton("立即下载") { _, _ ->
                    val downloadUtil = DownloadUtil(
                        this,
                        APK_URL,
                        fileName
                    )
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("以后再说") { _, _ ->
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
        builder.show()
    }
}