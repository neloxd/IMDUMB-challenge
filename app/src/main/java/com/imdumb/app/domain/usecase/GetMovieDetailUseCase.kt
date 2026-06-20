package com.imdumb.app.domain.usecase

import com.imdumb.app.domain.model.Movie
import com.imdumb.app.domain.model.MovieDetail
import com.imdumb.app.domain.repository.MovieRepository
import io.reactivex.Single
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(private val repository: MovieRepository) {
    operator fun invoke(movie: Movie): Single<MovieDetail> = repository.getMovieDetail(movie)
}
