package co.netguru.android.arlocalizeralternative.feature.camera

import co.netguru.android.arlocalizeralternative.feature.compass.CompassData

object ARLabelPositionUtils {

    private const val MAX_HORIZONTAL_ANGLE_VARIATION = 30f
    private const val MAX_VERTICAL_PITCH_VARIATION = 60f

    const val LOW_PASS_FILTER_ALPHA_PRECISE = 0.90f
    const val LOW_PASS_FILTER_ALPHA_NORMAL = 0.60f

    val VERTICAL_ANGLE_RANGE_MAX = 0f..MAX_VERTICAL_PITCH_VARIATION
    val VERTICAL_ANGLE_RANGE_MIN = -MAX_VERTICAL_PITCH_VARIATION..0f

    val HORIZONTAL_ANGLE_RANGE_MAX = (360f - MAX_HORIZONTAL_ANGLE_VARIATION - 10f)..360f
    val HORIZONTAL_ANGLE_RANGE_MIN = 0f..10f + MAX_HORIZONTAL_ANGLE_VARIATION

    fun calculatePositionX(compassData: CompassData, displayWidth: Int): Float {
        return when (compassData.currentDestinationAzimuth) {
            in HORIZONTAL_ANGLE_RANGE_MIN -> {
                displayWidth / 2 + compassData.currentDestinationAzimuth * displayWidth / 2 / MAX_HORIZONTAL_ANGLE_VARIATION
            }
            in HORIZONTAL_ANGLE_RANGE_MAX -> {
                displayWidth / 2 - (360f - compassData.currentDestinationAzimuth) * displayWidth / 2 / MAX_HORIZONTAL_ANGLE_VARIATION
            }
            else -> 0f
        }
    }

    fun calculatePositionY(compassData: CompassData, displayHeight: Int): Float {
        return when (compassData.orientationData.currentPitch) {
            in VERTICAL_ANGLE_RANGE_MIN -> {
                displayHeight / 2 - compassData.orientationData.currentPitch * displayHeight / 2 / MAX_VERTICAL_PITCH_VARIATION
            }
            in VERTICAL_ANGLE_RANGE_MAX -> {
                displayHeight / 2 - compassData.orientationData.currentPitch * displayHeight / 2 / MAX_VERTICAL_PITCH_VARIATION
            }
            else -> 0f
        }
    }

    fun adjustLowPassFilterAlphaValue(positionX: Float, displayWidth: Int): Float {
        val centerPosition = displayWidth / 2f
        return when (positionX) {
            in 0f..(centerPosition) -> calculateLowPassFilterAlphaBeforeCenter(
                centerPosition,
                positionX
            )
            in centerPosition..displayWidth.toFloat() -> calculateLowPassFilterAlphaAfterCenter(
                centerPosition,
                positionX
            )
            else -> LOW_PASS_FILTER_ALPHA_NORMAL
        }
    }

    private fun calculateLowPassFilterAlphaAfterCenter(
        centerPosition: Float,
        positionX: Float
    ) =
        (LOW_PASS_FILTER_ALPHA_NORMAL - LOW_PASS_FILTER_ALPHA_PRECISE) / centerPosition * positionX + 2 * LOW_PASS_FILTER_ALPHA_PRECISE - LOW_PASS_FILTER_ALPHA_NORMAL

    private fun calculateLowPassFilterAlphaBeforeCenter(
        centerPosition: Float,
        positionX: Float
    ) =
        (LOW_PASS_FILTER_ALPHA_PRECISE - LOW_PASS_FILTER_ALPHA_NORMAL) / centerPosition * positionX + LOW_PASS_FILTER_ALPHA_NORMAL

}