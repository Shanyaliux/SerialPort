package com.shanya.serialport

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val MSG_RECE_TYPE = 1
const val MSG_SEND_TYPE = 2

@Entity(tableName = "info_database")
data class Info(
    @PrimaryKey(autoGenerate = true) val id:Int,
    @ColumnInfo(name = "info_type")val type:Int,
    @ColumnInfo(name = "info_content")val content:String
)