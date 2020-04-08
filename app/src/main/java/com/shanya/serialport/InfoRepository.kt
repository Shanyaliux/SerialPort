package com.shanya.serialport

import androidx.lifecycle.LiveData

class InfoRepository(private val infoDao: InfoDao) {

    val allInfo: LiveData<List<Info>> = infoDao.getAllInfoList()

    suspend fun insert(info: Info){
        infoDao.insert(info)
    }
}