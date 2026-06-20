package com.imdumb.app.presentation.home

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.imdumb.app.BuildConfig
import com.imdumb.app.domain.usecase.GetMovieCategoriesUseCase
import com.imdumb.app.presentation.base.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class HomePresenter @Inject constructor(
    private val getMovieCategories: GetMovieCategoriesUseCase,
    private val analytics: FirebaseAnalytics
) : BasePresenter<HomeView>() {

    fun loadCategories() {
        // Pull-to-refresh replaces a previous in-flight request rather than racing it.
        disposables.clear()

        getMovieCategories(BuildConfig.SHOWS_PAGE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { view?.showLoading(true) }
            .doFinally { view?.showLoading(false) }
            .subscribeBy(
                onSuccess = { categories ->
                    analytics.logEvent(
                        "movie_categories_loaded",
                        Bundle().apply {
                            putInt("category_count", categories.size)
                            putString("environment", BuildConfig.FLAVOR)
                        }
                    )
                    if (categories.isEmpty()) {
                        view?.showEmpty()
                    } else {
                        view?.showCategories(categories)
                    }
                },
                onError = {
                    view?.showError(
                        "No pudimos cargar las películas. " +
                            "Revisa tu conexión e inténtalo otra vez."
                    )
                }
            )
            .addTo(disposables)
    }
}
