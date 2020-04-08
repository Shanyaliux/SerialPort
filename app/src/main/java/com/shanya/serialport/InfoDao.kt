package com.shanya.serialport

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface InfoDao {
    @Query("SELECT * from info_database ORDER BY id ASC")
    fun getAllInfoList(): LiveData<List<Info>>

    @Insert
    suspend fun insert(info: Info)

    @Query("DELETE FROM info_database")
    suspend fun deleteAll()
}