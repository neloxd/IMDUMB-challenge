package com.imdumb.app.di

import android.app.Application
import com.imdumb.app.presentation.detail.DetailActivity
import com.imdumb.app.presentation.home.HomeActivity
import com.imdumb.app.presentation.splash.SplashActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        NetworkModule::class,
        RepositoryModule::class
    ]
)
interface AppComponent {

    fun inject(activity: SplashActivity)

    fun inject(activity: HomeActivity)

    fun inject(activity: DetailActivity)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }
}
