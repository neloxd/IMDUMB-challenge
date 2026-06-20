package com.imdumb.app.data.repository

import com.imdumb.app.BuildConfig
import com.imdumb.app.data.local.AppConfigurationStorage
import com.imdumb.app.data.remote.firebase.FirebaseConfigurationDataSource
import com.imdumb.app.domain.model.AppConfiguration
import com.imdumb.app.domain.repository.AppConfigurationRepository
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppConfigurationRepositoryImpl @Inject constructor(
    private val remoteDataSource: FirebaseConfigurationDataSource,
    private val storage: AppConfigurationStorage
) : AppConfigurationRepository {

    override fun synchronize(): Single<AppConfiguration> = remoteDataSource.fetch()
        .timeout(
            BuildConfig.REMOTE_CONFIG_FETCH_TIMEOUT_SECONDS + LOCAL_FALLBACK_GRACE_SECONDS,
            TimeUnit.SECONDS
        )
        .doOnSuccess(storage::save)
        .onErrorReturn {
            storage.read().also(storage::save)
        }

    override fun cached(): AppConfiguration = storage.read()

    private companion object {
        const val LOCAL_FALLBACK_GRACE_SECONDS = 1L
    }
}
