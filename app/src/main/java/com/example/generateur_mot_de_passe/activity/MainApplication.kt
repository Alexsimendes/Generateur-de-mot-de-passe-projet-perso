package com.example.generateur_mot_de_passe.activity

import android.app.Application
import android.content.Context

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        var appContext: Context? = null
            private set
    }

}
