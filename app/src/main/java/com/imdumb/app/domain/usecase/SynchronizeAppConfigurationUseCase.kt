package com.imdumb.app.domain.usecase

import com.imdumb.app.domain.model.AppConfiguration
import com.imdumb.app.domain.repository.AppConfigurationRepository
import io.reactivex.Single
import javax.inject.Inject

class SynchronizeAppConfigurationUseCase @Inject constructor(private val repository: AppConfigurationRepository) {
    operator fun invoke(): Single<AppConfiguration> = repository.synchronize()
}
