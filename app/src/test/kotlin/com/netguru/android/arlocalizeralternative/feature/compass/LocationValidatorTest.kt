package com.netguru.android.arlocalizeralternative.feature.compass

import com.netguru.android.arlocalizeralternative.feature.arlocalizer.CoordinateType
import com.netguru.android.arlocalizeralternative.feature.arlocalizer.LocationValidator
import com.netguru.android.arlocalizeralternative.feature.arlocalizer.ValidationResult
import org.junit.Test

/**
 * Created by Mateusz on 12.04.2019.
 */
class LocationValidatorTest {

    private val locationValidator = LocationValidator()

    @Test
    fun `should return ValidationResultEmpty on null`() {
        val latitudeValidationResult = locationValidator.validateValue(null, CoordinateType.LATITUDE)
        val longitudeValidationResult = locationValidator.validateValue(null, CoordinateType.LONGITUDE)

        assert(latitudeValidationResult == ValidationResult.EMPTY_VALUE)
        assert(longitudeValidationResult == ValidationResult.EMPTY_VALUE)
    }

    @Test
    fun `should return correct value on latitude -90 to 90`() {
        val startValueResult = locationValidator.validateValue(-90.0, CoordinateType.LATITUDE)
        val middleValueResult = locationValidator.validateValue(0.0, CoordinateType.LATITUDE)
        val endValueResult = locationValidator.validateValue(90.0, CoordinateType.LATITUDE)

        assert(startValueResult == ValidationResult.CORRECT_VALUE)
        assert(middleValueResult == ValidationResult.CORRECT_VALUE)
        assert(endValueResult == ValidationResult.CORRECT_VALUE)
    }

    @Test
    fun `should return wrong value on latitude  less than -90 or more than 90`() {
        val startValueResult = locationValidator.validateValue(-91.0, CoordinateType.LATITUDE)
        val endValueResult = locationValidator.validateValue(91.0, CoordinateType.LATITUDE)

        assert(startValueResult == ValidationResult.WRONG_VALUE)
        assert(endValueResult == ValidationResult.WRONG_VALUE)
    }

    @Test
    fun `should return correct value on longitude -180 to 180`() {
        val startValueResult = locationValidator.validateValue(-180.0, CoordinateType.LONGITUDE)
        val middleValueResult = locationValidator.validateValue(0.0, CoordinateType.LONGITUDE)
        val endValueResult = locationValidator.validateValue(180.0, CoordinateType.LONGITUDE)

        assert(startValueResult == ValidationResult.CORRECT_VALUE)
        assert(middleValueResult == ValidationResult.CORRECT_VALUE)
        assert(endValueResult == ValidationResult.CORRECT_VALUE)
    }

    @Test
    fun `should return wrong value on longitude  less than -180 or more than 180`() {
        val startValueResult = locationValidator.validateValue(-181.0, CoordinateType.LONGITUDE)
        val endValueResult = locationValidator.validateValue(181.0, CoordinateType.LONGITUDE)

        assert(startValueResult == ValidationResult.WRONG_VALUE)
        assert(endValueResult == ValidationResult.WRONG_VALUE)
    }
}
