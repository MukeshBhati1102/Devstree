package com.example.myapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.data.PlaceData

@Database(
    entities = [PlaceData::class],
    version = 3,
    exportSchema = true
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun getPlaceDataDAO(): PlaceDataDAO

    companion object {

        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context?): AppDatabase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context!!,
                    AppDatabase::class.java,
                    "my_application.db"
                )
                    .allowMainThreadQueries() // recreate the database if necessary
//                    .fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE
        }
    }

    open fun destroyInstance() {
        INSTANCE = null
    }

}