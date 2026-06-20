package com.imdumb.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ShowImageDto(
    @SerializedName("id") val id: Long?,
    @SerializedName("type") val type: String?,
    @SerializedName("main") val isMain: Boolean?,
    @SerializedName("resolutions") val resolutions: ImageResolutionsDto?
)

data class ImageResolutionsDto(
    @SerializedName("original") val original: ImageResolutionDto?,
    @SerializedName("medium") val medium: ImageResolutionDto?
)

data class ImageResolutionDto(
    @SerializedName("url") val url: String?,
    @SerializedName("width") val width: Int?,
    @SerializedName("height") val height: Int?
)
