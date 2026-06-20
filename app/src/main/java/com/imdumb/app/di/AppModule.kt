package com.imdumb.app.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.imdumb.app.BuildConfig
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application.applicationContext

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences("imdumb_config", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(context: Context): FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    @Provides
    @Singleton
    fun provideRemoteConfig(): FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    @Provides
    @Singleton
    fun provideRemoteConfigSettings(): FirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings.Builder()
        .setMinimumFetchIntervalInSeconds(BuildConfig.REMOTE_CONFIG_MIN_FETCH_SECONDS)
        .setFetchTimeoutInSeconds(BuildConfig.REMOTE_CONFIG_FETCH_TIMEOUT_SECONDS)
        .build()
}
