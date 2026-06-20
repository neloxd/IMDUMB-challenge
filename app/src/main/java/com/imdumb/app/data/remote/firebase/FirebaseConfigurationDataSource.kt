package com.imdumb.app.data.remote.firebase

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.imdumb.app.R
import com.imdumb.app.domain.model.AppConfiguration
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseConfigurationDataSource @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
    private val settings: FirebaseRemoteConfigSettings
) {

    /**
     * Applies bundled defaults first, then attempts a Firebase fetch. If the network/project is
     * unavailable, the currently activated/default values are still returned so the challenge can
     * start without manual setup. The repository persists the resulting configuration locally.
     */
    fun fetch(): Single<AppConfiguration> = Single.create { emitter ->
        remoteConfig.setConfigSettingsAsync(settings)
            .addOnCompleteListener settings@{ settingsTask ->
                if (emitter.isDisposed) return@settings
                if (!settingsTask.isSuccessful) {
                    emitter.tryOnError(
                        settingsTask.exception
                            ?: IllegalStateException("Firebase settings could not be applied")
                    )
                    return@settings
                }

                remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
                    .addOnCompleteListener defaults@{ defaultsTask ->
                        if (emitter.isDisposed) return@defaults
                        if (!defaultsTask.isSuccessful) {
                            emitter.tryOnError(
                                defaultsTask.exception
                                    ?: IllegalStateException("Firebase defaults could not be applied")
                            )
                            return@defaults
                        }

                        remoteConfig.fetchAndActivate()
                            .addOnCompleteListener fetch@{ fetchTask ->
                                if (emitter.isDisposed) return@fetch

                                // A failed fetch still leaves activated values or XML defaults ready.
                                // This keeps startup deterministic while retaining a real fetch path.
                                if (!fetchTask.isSuccessful && fetchTask.exception == null) {
                                    emitter.tryOnError(
                                        IllegalStateException("Firebase Remote Config fetch failed")
                                    )
                                    return@fetch
                                }
                                emitter.onSuccess(readCurrentValues())
                            }
                    }
            }
    }

    private fun readCurrentValues(): AppConfiguration = AppConfiguration(
        welcomeMessage = remoteConfig.getString(KEY_WELCOME_MESSAGE)
            .ifBlank { AppConfiguration.DEFAULT.welcomeMessage },
        homeTitle = remoteConfig.getString(KEY_HOME_TITLE)
            .ifBlank { AppConfiguration.DEFAULT.homeTitle },
        recommendationsEnabled = remoteConfig.getBoolean(KEY_RECOMMENDATIONS_ENABLED)
    )

    private companion object {
        const val KEY_WELCOME_MESSAGE = "welcome_message"
        const val KEY_HOME_TITLE = "home_title"
        const val KEY_RECOMMENDATIONS_ENABLED = "recommendations_enabled"
    }
}
