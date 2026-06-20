package com.imdumb.app.data.mapper

import com.imdumb.app.data.remote.dto.CastCreditDto
import com.imdumb.app.data.remote.dto.ShowDto
import com.imdumb.app.data.remote.dto.ShowImageDto
import com.imdumb.app.domain.model.Movie
import com.imdumb.app.domain.model.MovieCategory
import com.imdumb.app.domain.model.MovieDetail
import java.util.Locale
import javax.inject.Inject

class TvMazeMapper @Inject constructor() {

    fun toCategories(shows: List<ShowDto>): List<MovieCategory> {
        val movies = shows.mapNotNull(::toMovie)
        if (movies.isEmpty()) return emptyList()

        val categories = mutableListOf<MovieCategory>()
        categories += MovieCategory(
            id = POPULAR_CATEGORY_ID,
            name = "Más populares",
            movies = movies
                .sortedByDescending { it.rating ?: 0.0 }
                .take(MAX_MOVIES_PER_CATEGORY)
        )

        movies
            .flatMap { movie -> movie.genres.map { genre -> genre to movie } }
            .groupBy(keySelector = { it.first }, valueTransform = { it.second })
            .entries
            .sortedWith(
                compareByDescending<Map.Entry<String, List<Movie>>> { it.value.size }
                    .thenBy { it.key }
            )
            .take(MAX_GENRE_CATEGORIES)
            .forEach { (genre, genreMovies) ->
                categories += MovieCategory(
                    id = genre.lowercase(Locale.ROOT).replace(" ", "-"),
                    name = genre,
                    movies = genreMovies
                        .distinctBy { it.id }
                        .take(MAX_MOVIES_PER_CATEGORY)
                )
            }

        return categories
    }

    fun toDetail(movie: Movie, cast: List<CastCreditDto>, images: List<ShowImageDto>): MovieDetail {
        val imageUrls = buildList {
            movie.posterUrl?.let(::add)
            images
                .sortedBy(::imagePriority)
                .mapNotNull { image ->
                    image.resolutions?.original?.url ?: image.resolutions?.medium?.url
                }
                .forEach(::add)
        }.filter { it.isNotBlank() }
            .distinct()
            .take(MAX_DETAIL_IMAGES)

        val actors = cast
            .mapNotNull { it.person?.name?.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .take(MAX_ACTORS)

        return MovieDetail(
            movie = movie,
            imageUrls = imageUrls,
            actors = actors
        )
    }

    private fun imagePriority(image: ShowImageDto): Int = when {
        image.isMain == true -> 0
        image.type == IMAGE_TYPE_BACKGROUND -> 1
        image.type == IMAGE_TYPE_POSTER -> 2
        else -> 3
    }

    private fun toMovie(dto: ShowDto): Movie? {
        val title = dto.name?.trim().orEmpty()
        if (title.isEmpty()) return null

        return Movie(
            id = dto.id,
            title = title,
            rating = dto.rating?.average,
            summaryHtml = dto.summary?.trim().orEmpty().ifEmpty {
                "<p>Sin descripción disponible.</p>"
            },
            posterUrl = dto.image?.original ?: dto.image?.medium,
            genres = dto.genres.orEmpty()
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .distinct()
        )
    }

    private companion object {
        const val POPULAR_CATEGORY_ID = "popular"
        const val MAX_GENRE_CATEGORIES = 8
        const val MAX_MOVIES_PER_CATEGORY = 16
        const val MAX_DETAIL_IMAGES = 8
        const val MAX_ACTORS = 20
        const val IMAGE_TYPE_BACKGROUND = "background"
        const val IMAGE_TYPE_POSTER = "poster"
    }
}
