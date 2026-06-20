package com.imdumb.app.data.repository

import com.imdumb.app.data.mapper.TvMazeMapper
import com.imdumb.app.data.remote.api.TvMazeApi
import com.imdumb.app.domain.model.Movie
import com.imdumb.app.domain.model.MovieCategory
import com.imdumb.app.domain.model.MovieDetail
import com.imdumb.app.domain.repository.MovieRepository
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(private val api: TvMazeApi, private val mapper: TvMazeMapper) :
    MovieRepository {

    override fun getCategories(page: Int): Single<List<MovieCategory>> = api.getShows(page).map(mapper::toCategories)

    override fun getMovieDetail(movie: Movie): Single<MovieDetail> {
        val cast = api.getCast(movie.id).onErrorReturnItem(emptyList())
        val images = api.getImages(movie.id).onErrorReturnItem(emptyList())

        return Single.zip(cast, images) { castResponse, imageResponse ->
            mapper.toDetail(movie, castResponse, imageResponse)
        }
    }
}
