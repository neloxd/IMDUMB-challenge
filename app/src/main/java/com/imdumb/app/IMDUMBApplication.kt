package com.imdumb.app

import android.app.Application
import com.google.firebase.FirebaseApp
import com.imdumb.app.di.AppComponent
import com.imdumb.app.di.DaggerAppComponent

class IMDUMBApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        appComponent
    }
}
