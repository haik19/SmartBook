package com.guess.hk.smartbook.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.guess.hk.smartbook.model.BookKey

@Database(entities = [BookKey::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun booksKeyDao(): BookKeyDao
}