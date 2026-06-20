package com.imdumb.app.presentation.home

import com.imdumb.app.domain.model.MovieCategory
import com.imdumb.app.presentation.base.MvpView

interface HomeView : MvpView {
    fun showLoading(show: Boolean)
    fun showCategories(categories: List<MovieCategory>)
    fun showError(message: String)
    fun showEmpty()
}
