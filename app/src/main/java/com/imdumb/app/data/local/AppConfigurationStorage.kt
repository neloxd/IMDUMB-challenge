package com.imdumb.app.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.imdumb.app.domain.model.AppConfiguration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppConfigurationStorage @Inject constructor(private val preferences: SharedPreferences) {

    fun save(configuration: AppConfiguration) {
        preferences.edit {
            putString(KEY_WELCOME_MESSAGE, configuration.welcomeMessage)
            putString(KEY_HOME_TITLE, configuration.homeTitle)
            putBoolean(KEY_RECOMMENDATIONS_ENABLED, configuration.recommendationsEnabled)
            putBoolean(KEY_HAS_VALUE, true)
        }
    }

    fun read(): AppConfiguration {
        if (!preferences.getBoolean(KEY_HAS_VALUE, false)) {
            return AppConfiguration.DEFAULT
        }
        return AppConfiguration(
            welcomeMessage = preferences.getString(
                KEY_WELCOME_MESSAGE,
                AppConfiguration.DEFAULT.welcomeMessage
            ).orEmpty().ifBlank { AppConfiguration.DEFAULT.welcomeMessage },
            homeTitle = preferences.getString(
                KEY_HOME_TITLE,
                AppConfiguration.DEFAULT.homeTitle
            ).orEmpty().ifBlank { AppConfiguration.DEFAULT.homeTitle },
            recommendationsEnabled = preferences.getBoolean(
                KEY_RECOMMENDATIONS_ENABLED,
                AppConfiguration.DEFAULT.recommendationsEnabled
            )
        )
    }

    private companion object {
        const val KEY_WELCOME_MESSAGE = "welcome_message"
        const val KEY_HOME_TITLE = "home_title"
        const val KEY_RECOMMENDATIONS_ENABLED = "recommendations_enabled"
        const val KEY_HAS_VALUE = "has_value"
    }
}
