package com.netguru.arlocalizerview.location

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class LocationData(val latitude: Double, val longitude: Double) : Parcelable
