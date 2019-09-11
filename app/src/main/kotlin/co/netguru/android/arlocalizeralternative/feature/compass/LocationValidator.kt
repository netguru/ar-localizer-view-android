package co.netguru.android.arlocalizeralternative.feature.compass

import co.netguru.android.arlocalizeralternative.feature.location.CoordinateType
import co.netguru.android.arlocalizeralternative.feature.location.ValidationResult
import javax.inject.Inject

class LocationValidator @Inject constructor() {

    fun validateValue(value: Double?, coordinateType: CoordinateType): ValidationResult {
        if(value == null) return ValidationResult.EMPTY_VALUE
        return when(coordinateType) {
            CoordinateType.LATITUDE -> validateLatitude(value)
            CoordinateType.LONGITUDE -> validateLongitude(value)
        }
    }

    private fun validateLatitude(latitude: Double): ValidationResult {
        return if (latitude < -90 || latitude > 90) ValidationResult.WRONG_VALUE
        else ValidationResult.CORRECT_VALUE
    }

    private fun validateLongitude(longitude: Double): ValidationResult {
        return if (longitude < -180 || longitude > 180) ValidationResult.WRONG_VALUE
        else ValidationResult.CORRECT_VALUE
    }
}