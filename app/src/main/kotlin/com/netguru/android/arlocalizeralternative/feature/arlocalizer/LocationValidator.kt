package com.netguru.android.arlocalizeralternative.feature.arlocalizer

import javax.inject.Inject

class LocationValidator @Inject constructor() {

    companion object {
        private const val MAX_LATITUDE = 90
        private const val MIN_LATITUDE = -90
        private const val MAX_LONGITUDE = 180
        private const val MIN_LONGITUDE = -180
    }

    fun validateValue(value: Double?, coordinateType: CoordinateType): ValidationResult {
        if (value == null) return ValidationResult.EMPTY_VALUE
        return when (coordinateType) {
            CoordinateType.LATITUDE -> validateLatitude(value)
            CoordinateType.LONGITUDE -> validateLongitude(value)
        }
    }

    private fun validateLatitude(latitude: Double): ValidationResult {
        return if (latitude < MIN_LATITUDE || latitude > MAX_LATITUDE) ValidationResult.WRONG_VALUE
        else ValidationResult.CORRECT_VALUE
    }

    private fun validateLongitude(longitude: Double): ValidationResult {
        return if (longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE) ValidationResult.WRONG_VALUE
        else ValidationResult.CORRECT_VALUE
    }
}
