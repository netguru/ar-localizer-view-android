package com.netguru.android.arlocalizeralternative.feature.location.data

import com.google.gson.annotations.SerializedName

data class OSMResponse(
    @SerializedName("elements") val elements: List<LocationNode>
)
