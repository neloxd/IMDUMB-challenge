package com.imdumb.app.presentation.splash

import com.imdumb.app.presentation.base.MvpView

interface SplashView : MvpView {
    fun showLoading(show: Boolean)
    fun showStatus(message: String)
    fun openHome(homeTitle: String)
}
