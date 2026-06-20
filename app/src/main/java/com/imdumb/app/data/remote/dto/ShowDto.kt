package com.imdumb.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ShowDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String?,
    @SerializedName("genres") val genres: List<String>?,
    @SerializedName("rating") val rating: RatingDto?,
    @SerializedName("image") val image: ShowPosterDto?,
    @SerializedName("summary") val summary: String?
)

data class RatingDto(@SerializedName("average") val average: Double?)

data class ShowPosterDto(
    @SerializedName("medium") val medium: String?,
    @SerializedName("original") val original: String?
)
