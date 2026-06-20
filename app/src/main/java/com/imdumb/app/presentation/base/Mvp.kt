package com.imdumb.app.presentation.base

import io.reactivex.disposables.CompositeDisposable

interface MvpView

interface MvpPresenter<V : MvpView> {
    fun attach(view: V)
    fun detach()
}

abstract class BasePresenter<V : MvpView> : MvpPresenter<V> {

    protected var view: V? = null
        private set

    protected val disposables = CompositeDisposable()

    override fun attach(view: V) {
        this.view = view
    }

    override fun detach() {
        disposables.clear()
        view = null
    }
}
