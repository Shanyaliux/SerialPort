package com.shanya.serialport

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Info::class),version = 1,exportSchema = false)
abstract class InfoDatabase: RoomDatabase() {

    abstract fun infoDao(): InfoDao

    private class InfoDatabaseCallback(private val scope: CoroutineScope): RoomDatabase.Callback(){
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let {
                scope.launch {
                    val infoDao = it.infoDao()

                    infoDao.deleteAll()
                }
            }
        }
    }

    companion object{
        @Volatile
        private var INSTANCE: InfoDatabase? = null

        fun getDatabase(context: Context,scope: CoroutineScope): InfoDatabase{
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InfoDatabase::class.java,
                    "info_database"
                ).addCallback(InfoDatabaseCallback(scope)).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}