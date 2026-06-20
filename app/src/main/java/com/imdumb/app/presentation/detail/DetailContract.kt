package com.imdumb.app.presentation.detail

import com.imdumb.app.domain.model.MovieDetail
import com.imdumb.app.presentation.base.MvpView

interface DetailView : MvpView {
    fun showLoading(show: Boolean)
    fun showDetail(detail: MovieDetail)
    fun showError(message: String)
    fun setRecommendationEnabled(enabled: Boolean)
    fun showRecommendationSuccess()
}
