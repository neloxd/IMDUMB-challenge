package com.imdumb.app.presentation.detail

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.imdumb.app.domain.model.Movie
import com.imdumb.app.domain.usecase.GetCachedAppConfigurationUseCase
import com.imdumb.app.domain.usecase.GetMovieDetailUseCase
import com.imdumb.app.presentation.base.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class DetailPresenter @Inject constructor(
    private val getMovieDetail: GetMovieDetailUseCase,
    private val getCachedConfiguration: GetCachedAppConfigurationUseCase,
    private val analytics: FirebaseAnalytics
) : BasePresenter<DetailView>() {

    fun load(movie: Movie) {
        view?.setRecommendationEnabled(
            getCachedConfiguration().recommendationsEnabled
        )

        getMovieDetail(movie)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { view?.showLoading(true) }
            .doFinally { view?.showLoading(false) }
            .subscribeBy(
                onSuccess = { detail ->
                    analytics.logEvent(
                        FirebaseAnalytics.Event.SELECT_CONTENT,
                        Bundle().apply {
                            putString(FirebaseAnalytics.Param.ITEM_ID, movie.id.toString())
                            putString(FirebaseAnalytics.Param.ITEM_NAME, movie.title)
                            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "movie")
                        }
                    )
                    view?.showDetail(detail)
                },
                onError = {
                    view?.showError("No pudimos completar el detalle de la película.")
                }
            )
            .addTo(disposables)
    }

    fun recommend(movie: Movie, comment: String) {
        if (comment.isBlank()) return

        analytics.logEvent(
            "movie_recommended",
            Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, movie.id.toString())
                putString(FirebaseAnalytics.Param.ITEM_NAME, movie.title)
                putInt("comment_length", comment.length)
            }
        )
        view?.showRecommendationSuccess()
    }
}
