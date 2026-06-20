package com.imdumb.app.domain.usecase

import com.imdumb.app.domain.model.AppConfiguration
import com.imdumb.app.domain.repository.AppConfigurationRepository
import javax.inject.Inject

class GetCachedAppConfigurationUseCase @Inject constructor(private val repository: AppConfigurationRepository) {
    operator fun invoke(): AppConfiguration = repository.cached()
}
