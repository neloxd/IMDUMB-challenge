package com.imdumb.app.data.mapper

import com.imdumb.app.data.remote.dto.CastCreditDto
import com.imdumb.app.data.remote.dto.ImageResolutionDto
import com.imdumb.app.data.remote.dto.ImageResolutionsDto
import com.imdumb.app.data.remote.dto.PersonDto
import com.imdumb.app.data.remote.dto.RatingDto
import com.imdumb.app.data.remote.dto.ShowDto
import com.imdumb.app.data.remote.dto.ShowImageDto
import com.imdumb.app.data.remote.dto.ShowPosterDto
import com.imdumb.app.domain.model.Movie
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TvMazeMapperTest {

    private val mapper = TvMazeMapper()

    @Test
    fun `categories contain a rating-sorted popular lane and genre lanes`() {
        val shows = listOf(
            show(id = 1, name = "Drama A", genres = listOf("Drama"), rating = 7.0),
            show(id = 2, name = "Comedy", genres = listOf("Comedy"), rating = 9.2),
            show(id = 3, name = "Drama B", genres = listOf("Drama"), rating = 8.1)
        )

        val categories = mapper.toCategories(shows)

        assertEquals("popular", categories.first().id)
        assertEquals(listOf(2L, 3L, 1L), categories.first().movies.map { it.id })
        assertEquals(listOf(1L, 3L), categories.first { it.name == "Drama" }.movies.map { it.id })
        assertEquals(listOf(2L), categories.first { it.name == "Comedy" }.movies.map { it.id })
    }

    @Test
    fun `invalid shows are discarded and missing summary gets safe html`() {
        val shows = listOf(
            show(id = 1, name = "  ", genres = emptyList(), rating = null),
            show(id = 2, name = "Valid", genres = listOf("", " Drama "), rating = null)
        )

        val categories = mapper.toCategories(shows)
        val movie = categories.first().movies.single()

        assertEquals("Valid", movie.title)
        assertEquals(listOf("Drama"), movie.genres)
        assertTrue(movie.summaryHtml.contains("Sin descripción"))
    }

    @Test
    fun `detail merges poster and gallery while deduplicating actors and images`() {
        val movie = Movie(
            id = 7,
            title = "A Movie",
            rating = 8.0,
            summaryHtml = "<p>Summary</p>",
            posterUrl = "https://img/poster.jpg",
            genres = listOf("Drama")
        )
        val cast = listOf(
            CastCreditDto(PersonDto(1, "Ada")),
            CastCreditDto(PersonDto(2, " Ada ")),
            CastCreditDto(PersonDto(3, "Bob"))
        )
        val images = listOf(
            image(id = 1, url = "https://img/poster.jpg", main = false),
            image(id = 2, url = "https://img/main.jpg", main = true)
        )

        val detail = mapper.toDetail(movie, cast, images)

        assertEquals(listOf("https://img/poster.jpg", "https://img/main.jpg"), detail.imageUrls)
        assertEquals(listOf("Ada", "Bob"), detail.actors)
    }

    private fun show(id: Long, name: String, genres: List<String>, rating: Double?) = ShowDto(
        id = id,
        name = name,
        genres = genres,
        rating = RatingDto(rating),
        image = ShowPosterDto(
            medium = "https://img/$id-medium.jpg",
            original = "https://img/$id-original.jpg"
        ),
        summary = null
    )

    private fun image(id: Long, url: String, main: Boolean) = ShowImageDto(
        id = id,
        type = "background",
        isMain = main,
        resolutions = ImageResolutionsDto(
            original = ImageResolutionDto(url, 1280, 720),
            medium = null
        )
    )
}
