package com.guess.hk.smartbook

import android.app.Application
import androidx.room.Room
import com.google.firebase.FirebaseApp
import com.guess.hk.smartbook.db.AppDatabase

class SmartBookApplication : Application() {

    lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        db = Room.databaseBuilder(this, AppDatabase::class.java, "keysDb").allowMainThreadQueries().build()
    }

}