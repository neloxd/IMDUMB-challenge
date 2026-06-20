package com.imdumb.app.presentation.splash

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.imdumb.app.domain.usecase.SynchronizeAppConfigurationUseCase
import com.imdumb.app.presentation.base.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SplashPresenter @Inject constructor(
    private val synchronizeConfiguration: SynchronizeAppConfigurationUseCase,
    private val analytics: FirebaseAnalytics
) : BasePresenter<SplashView>() {

    fun start() {
        synchronizeConfiguration()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { view?.showLoading(true) }
            .doOnSuccess { configuration ->
                analytics.logEvent(
                    "startup_configuration_loaded",
                    Bundle().apply {
                        putBoolean(
                            "recommendations_enabled",
                            configuration.recommendationsEnabled
                        )
                    }
                )
                view?.showStatus(configuration.welcomeMessage)
            }
            .delay(WELCOME_DISPLAY_TIME_MS, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { view?.showLoading(false) }
            .subscribeBy(
                onSuccess = { configuration ->
                    view?.openHome(configuration.homeTitle)
                },
                onError = {
                    view?.showStatus("No fue posible preparar la aplicación")
                    view?.openHome("Categorías")
                }
            )
            .addTo(disposables)
    }

    private companion object {
        const val WELCOME_DISPLAY_TIME_MS = 350L
    }
}
