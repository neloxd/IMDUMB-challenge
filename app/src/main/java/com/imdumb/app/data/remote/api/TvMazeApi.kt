package com.imdumb.app.data.remote.api

import com.imdumb.app.data.remote.dto.CastCreditDto
import com.imdumb.app.data.remote.dto.ShowDto
import com.imdumb.app.data.remote.dto.ShowImageDto
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TvMazeApi {

    @GET("shows")
    fun getShows(@Query("page") page: Int): Single<List<ShowDto>>

    @GET("shows/{id}/cast")
    fun getCast(@Path("id") showId: Long): Single<List<CastCreditDto>>

    @GET("shows/{id}/images")
    fun getImages(@Path("id") showId: Long): Single<List<ShowImageDto>>
}
