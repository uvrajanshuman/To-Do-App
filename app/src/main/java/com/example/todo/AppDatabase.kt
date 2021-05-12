package com.example.todo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ToDoModel::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun toDoDao() : ToDoDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        //To create single instance only.
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}