package com.imdumb.app.domain.repository

import com.imdumb.app.domain.model.AppConfiguration
import io.reactivex.Single

interface AppConfigurationRepository {
    fun synchronize(): Single<AppConfiguration>
    fun cached(): AppConfiguration
}
