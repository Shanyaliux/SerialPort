package com.shanya.serialport.startactivity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.shanya.downloadutil.DownloadUtil
import com.shanya.serialport.*
import com.shanya.serialport.update.VersionInfo
import com.shanya.serialport.update.VolleySingleton
import kotlinx.android.synthetic.main.activity_splash.*
import java.io.File
import java.lang.Exception
import java.util.*

const val DOWNLOAD_SUCCESS = 0x654
const val DOWNLOAD_FAIL = 0x655
const val DOWNLOAD_PROGRESS = 0x656
const val REQUEST_INSTALL_PERMISSION = 0x684
const val JSON_URL = "https://shanya-01.coding.net/p/SerialPort/d/SerialPort/git/raw/master/update.json"

class SplashActivity : AppCompatActivity() {

    private var apkName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val animationTag = AnimationUtils.loadAnimation(this,R.anim.slide_from_left)
        animationTag.duration = 600
        val animationLogo = AnimationUtils.loadAnimation(this,R.anim.slide_from_right)
        animationLogo.duration = 600
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

            Handler().postDelayed(Runnable {

                val animationCheckText = AnimationUtils.loadAnimation(this,R.anim.slide_from_bottom)
                animationCheckText.duration = 500
                textViewCheckUpdate.animation = animationCheckText
                textViewCheckUpdate.visibility = View.VISIBLE
                progressBarDownload.isIndeterminate = true
                progressBarDownload.visibility = View.VISIBLE

                Handler().postDelayed(Runnable {
                    val stringRequest = StringRequest(
                        Request.Method.GET,
                        JSON_URL,
                        Response.Listener {
                            val versionCode = Gson().fromJson(it,VersionInfo::class.java).versionCode
                            val updateContent = Gson().fromJson(it,VersionInfo::class.java).updateContent
                            val fileName = Gson().fromJson(it,VersionInfo::class.java).fileName
                            val downloadUrl = Gson().fromJson(it,VersionInfo::class.java).downloadUrl
                            if (BuildConfig.VERSION_CODE < versionCode) {
                                //显示更新对话框
                                updateDialog(updateContent, fileName,downloadUrl)
                            }else{

                                Handler().postDelayed(Runnable {
                                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish() },1000)

                            }
                        },
                        Response.ErrorListener {

                            Log.d("VolleyErrorListener",it.toString())
                        })
                    VolleySingleton.getInstance(application).requestQueue.add(stringRequest)
                },1000)

            },1500)



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

                        val animationCheckText = AnimationUtils.loadAnimation(this,R.anim.slide_from_bottom)
                        animationCheckText.duration = 500
                        textViewCheckUpdate.animation = animationCheckText
                        textViewCheckUpdate.visibility = View.VISIBLE
                        progressBarDownload.isIndeterminate = true
                        progressBarDownload.visibility = View.VISIBLE

                        Handler().postDelayed(Runnable {
                            val stringRequest = StringRequest(
                                Request.Method.GET,
                                JSON_URL,
                                Response.Listener {
                                    val versionCode = Gson().fromJson(it,VersionInfo::class.java).versionCode
                                    val updateContent = Gson().fromJson(it,VersionInfo::class.java).updateContent
                                    val fileName = Gson().fromJson(it,VersionInfo::class.java).fileName
                                    val downloadUrl = Gson().fromJson(it,VersionInfo::class.java).downloadUrl
                                    if (BuildConfig.VERSION_CODE < versionCode) {

                                        updateDialog(updateContent, fileName,downloadUrl)

                                    }else{
                                        Handler().postDelayed(Runnable {
                                            val intent = Intent(this@SplashActivity, MainActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        },1000)

                                    }
                                },
                                Response.ErrorListener {

                                    Log.d("VolleyErrorListener",it.toString())
                                })
                            VolleySingleton.getInstance(application).requestQueue.add(stringRequest)
                        },1000)


                    }
                }
            }
        }
    }

    private fun updateDialog(updateContent: String, fileName: String,downloadUrl : String) {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this)
                .setTitle("发现新版本")
                .setMessage(updateContent)
                .setPositiveButton("立即下载") { _, _ ->
                    val downloadUtil = DownloadUtil.getInstance(
                        downloadUrl,
                        this@SplashActivity.externalCacheDir.toString() + File.separator + fileName,
                        6,
                        object :DownloadUtil.OnDownloadListener{
                            override fun onDownloadFailed(e: Exception?) {
                                val message = Message.obtain()
                                message.what = DOWNLOAD_FAIL
                                handler.sendMessage(message)
                            }

                            override fun onDownloadSuccess() {
                                val message = Message.obtain()
                                message.what = DOWNLOAD_SUCCESS
                                message.obj = fileName
                                handler.sendMessage(message)
                            }

                            override fun onDownloadProgress(progress: Int) {
                                val message = Message.obtain()
                                message.what = DOWNLOAD_PROGRESS
                                message.arg1 = progress
                                handler.sendMessage(message)
                            }

                        })

                    downloadUtil.download()
                }
                .setNegativeButton("以后再说") { _, _ ->
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
        builder.show()
    }

    private fun installDialog(path: String){
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("下载完成，是否立即安装\n若之后安装，文件存于\n“$path”")
                .setPositiveButton("立即安装"){_,_->
                    checkInstallPermission()
                    installApk(path)
                }
                .setNegativeButton("之后安装"){_,_->
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
        builder.show()
    }

    private fun installApk(path: String){
        val apk = File(path)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileProvider", apk)
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
        }else{
            intent.setDataAndType(Uri.fromFile(apk),"application/vnd.android.package-archive")
        }
        startActivity(intent)
    }

    private fun checkInstallPermission(){
        val intent = Intent()
        val packageUri = Uri.parse("package:" + this.packageName)
        intent.data = packageUri
        if (Build.VERSION.SDK_INT >= 26){
            if (!packageManager.canRequestPackageInstalls()){
                intent.action = Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES
                startActivityForResult(intent, REQUEST_INSTALL_PERMISSION)
                Toast.makeText(this,"请打开未知来源权限",Toast.LENGTH_SHORT).show()
            }
        }

    }

    val handler = object :Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                DOWNLOAD_FAIL -> {
                    Toast.makeText(this@SplashActivity,"下载失败",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                DOWNLOAD_SUCCESS -> {
                    Toast.makeText(this@SplashActivity,"下载成功",Toast.LENGTH_SHORT).show()
                    apkName = msg.obj as String
                    installDialog(this@SplashActivity.externalCacheDir.toString() + File.separator + apkName)
                }
                DOWNLOAD_PROGRESS -> {
                    progressBarDownload.isIndeterminate = false
                    progressBarDownload.progress = msg.arg1
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_INSTALL_PERMISSION){
            installDialog(this@SplashActivity.externalCacheDir.toString() + File.separator + apkName)
        }
    }
}
