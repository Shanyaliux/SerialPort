package com.shanya.serialport.update

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import android.os.Message
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.shanya.serialport.BuildConfig
import com.shanya.serialport.UPDATE_CODE_NO
import com.shanya.serialport.UPDATE_CODE_YES

class Update {
    companion object{
        fun checkUpdate(context: Context){
            val stringRequest = StringRequest(
                Request.Method.GET,
                "https://raw.githubusercontent.com/Shanyaliux/SerialPort/master/update/update.json",
                Response.Listener {
                    val versionCode = Gson().fromJson(it,VersionInfo::class.java).versionCode
                    val updateContent = Gson().fromJson(it,VersionInfo::class.java).updateContent
                    val fileName = Gson().fromJson(it,VersionInfo::class.java).fileName
                    if (BuildConfig.VERSION_CODE < versionCode) {
                        val builder: AlertDialog.Builder =
                            AlertDialog.Builder(context)
                                .setTitle("发现新版本")
                                .setMessage(updateContent)
                                .setPositiveButton("立即下载",
                                    DialogInterface.OnClickListener { dialog, which ->
                                        val downloadUtil = DownloadUtil(
                                            context,
                                            "https://github.com/Shanyaliux/AppUpdate/releases/download/test1/app-debug.apk",
                                            fileName
                                        )
                                    })
                                .setNegativeButton("以后再说",
                                    DialogInterface.OnClickListener { dialog, which -> })
                        builder.show()

                    }else{

                    }
                },
                Response.ErrorListener {
                    val handler = Handler()
                    val msg = Message.obtain()
                    msg.what = UPDATE_CODE_NO
                    handler.sendMessage(msg)
                    Log.d("VolleyErrorListener",it.toString())
                })
            VolleySingleton.getInstance(context).requestQueue.add(stringRequest)
        }
    }

}