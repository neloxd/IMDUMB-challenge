package com.imdumb.app.di

import com.imdumb.app.data.repository.AppConfigurationRepositoryImpl
import com.imdumb.app.data.repository.MovieRepositoryImpl
import com.imdumb.app.domain.repository.AppConfigurationRepository
import com.imdumb.app.domain.repository.MovieRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepository(implementation: MovieRepositoryImpl): MovieRepository

    @Binds
    @Singleton
    abstract fun bindAppConfigurationRepository(
        implementation: AppConfigurationRepositoryImpl
    ): AppConfigurationRepository
}
