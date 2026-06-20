package com.imdumb.app.domain.usecase

import com.imdumb.app.domain.model.MovieCategory
import com.imdumb.app.domain.repository.MovieRepository
import io.reactivex.Single
import javax.inject.Inject

class GetMovieCategoriesUseCase @Inject constructor(private val repository: MovieRepository) {
    operator fun invoke(page: Int): Single<List<MovieCategory>> = repository.getCategories(page)
}
