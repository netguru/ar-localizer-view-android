package co.netguru.arlocalizer.location

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class LocationData(val latitude: Double, val longitude: Double) : Parcelable
