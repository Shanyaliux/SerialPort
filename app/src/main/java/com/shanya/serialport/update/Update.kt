package com.shanya.serialport.update

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.shanya.serialport.BuildConfig
import com.shanya.serialport.UPDATE_CODE

class Update {
    companion object{
        fun checkUpdate(context: Context){
            val stringRequest = StringRequest(
                Request.Method.GET,
                "https://raw.githubusercontent.com/Shanyaliux/SerialPort/master/update/update.json",
                Response.Listener {
                    val versionCode = Gson().fromJson(it,VersionInfo::class.java).versionCode
                    if (BuildConfig.VERSION_CODE < versionCode) {
                        val handler = Handler()
                        val msg = Message()
                        msg.what = UPDATE_CODE
                        msg.obj = Gson().fromJson(it, VersionInfo::class.java)
                        handler.sendMessage(msg)
                    }
                },
                Response.ErrorListener {
                    Log.d("VolleyErrorListener",it.toString())
                })
            VolleySingleton.getInstance(context).requestQueue.add(stringRequest)
        }
    }

}