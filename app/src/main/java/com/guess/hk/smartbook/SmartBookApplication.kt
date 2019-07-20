package com.guess.hk.smartbook

import android.app.Application
import com.google.firebase.FirebaseApp

class SmartBookApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }

}