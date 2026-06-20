package com.imdumb.app.domain.repository

import com.imdumb.app.domain.model.Movie
import com.imdumb.app.domain.model.MovieCategory
import com.imdumb.app.domain.model.MovieDetail
import io.reactivex.Single

interface MovieRepository {
    fun getCategories(page: Int): Single<List<MovieCategory>>
    fun getMovieDetail(movie: Movie): Single<MovieDetail>
}
