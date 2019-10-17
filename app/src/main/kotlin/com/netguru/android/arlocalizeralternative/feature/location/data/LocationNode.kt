package com.netguru.android.arlocalizeralternative.feature.location.data

import com.google.gson.annotations.SerializedName

data class LocationNode(
    @SerializedName("lat") val latitude: Double,
    @SerializedName("lon") val longitude: Double
)
