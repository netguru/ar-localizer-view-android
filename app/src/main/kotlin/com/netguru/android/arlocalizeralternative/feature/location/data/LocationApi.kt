package com.netguru.android.arlocalizeralternative.feature.location.data

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationApi {

    @GET("interpreter/")
    fun nodes(@Query("data") data: String): Single<OSMResponse>
}
