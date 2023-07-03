package com.example.appdev_project.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

class MyRoomDatabaseCallback : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        // Perform your initial data insertion here
        // For example, insert default data into your tables
    }
}