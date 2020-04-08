package com.shanya.serialport

import android.app.Application
import android.os.Handler
import android.os.Message
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter

class InfoViewModel(application: Application) : AndroidViewModel(application) {

    private val infoRepository: InfoRepository
    val allInfo: LiveData<List<Info>>

    init {
        val infoDao = InfoDatabase.getDatabase(application,viewModelScope).infoDao()
        infoRepository = InfoRepository(infoDao)
        allInfo = infoRepository.allInfo
    }

    fun insert(info: Info) = viewModelScope.launch {
        infoRepository.insert(info)
    }

    inner class ReceiveThread(private val handler: MyHandler, private val inputStream: InputStream): Thread(){
        override fun run() {
            super.run()
            var len = 0
            var flag = false
            var data = ByteArray(0)
            while (true) {
                sleep(100)
                len = inputStream.available()
                while (len != 0) {
                    flag = true
                    data = ByteArray(len)
                    inputStream.read(data)
                    sleep(10)
                    len = inputStream.available()
                }
                if (flag) {
                    println(String(data))
                    val message = Message.obtain()
                    message.what = MSG_RECE_TYPE
                    message.obj = String(data, Charsets.UTF_8)
                    handler.sendMessage(message)
                    flag = false
                }
            }
        }
    }
}