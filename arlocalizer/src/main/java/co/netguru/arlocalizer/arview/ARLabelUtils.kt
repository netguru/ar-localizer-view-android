package co.netguru.arlocalizer.arview

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.view.animation.AccelerateInterpolator
import co.netguru.arlocalizer.compass.CompassData
import kotlin.math.roundToInt

@Suppress("MagicNumber")
internal object ARLabelUtils {

    private const val MAX_HORIZONTAL_ANGLE_VARIATION = 30f
    private const val MAX_VERTICAL_PITCH_VARIATION = 60f

    const val LOW_PASS_FILTER_ALPHA_PRECISE = 0.90f
    const val LOW_PASS_FILTER_ALPHA_NORMAL = 0.60f

    val VERTICAL_ANGLE_RANGE_MAX = 0f..MAX_VERTICAL_PITCH_VARIATION
    val VERTICAL_ANGLE_RANGE_MIN = -MAX_VERTICAL_PITCH_VARIATION..0f

    val HORIZONTAL_ANGLE_RANGE_MAX = 360f - MAX_HORIZONTAL_ANGLE_VARIATION - 10f..360f
    val HORIZONTAL_ANGLE_RANGE_MIN = 0f..10f + MAX_HORIZONTAL_ANGLE_VARIATION

    private const val PROPERTY_SIZE = "size"
    private const val ANIMATED_VALUE_MAX_SIZE = 120
    private const val ANIMATION_DURATION = 1000L
    private const val ACCELERATE_INTERPOLATOR_FACTOR = 2.5f
    const val MAX_ALPHA_VALUE = 255f
    const val ALPHA_DELTA = 155f

    fun calculatePositionX(destinationAzimuth: Float, viewWidth: Int): Float {
        return when (destinationAzimuth) {
            in HORIZONTAL_ANGLE_RANGE_MIN -> {
                viewWidth / 2 + destinationAzimuth * viewWidth /
                        2 / MAX_HORIZONTAL_ANGLE_VARIATION
            }
            in HORIZONTAL_ANGLE_RANGE_MAX -> {
                viewWidth / 2 - (360f - destinationAzimuth) * viewWidth /
                        2 / MAX_HORIZONTAL_ANGLE_VARIATION
            }
            else -> 0f
        }
    }

    fun calculatePositionY(currentPitch: Float, viewHeight: Int): Float {
        return when (currentPitch) {
            in VERTICAL_ANGLE_RANGE_MIN -> {
                viewHeight / 2 - currentPitch * viewHeight /
                        2 / MAX_VERTICAL_PITCH_VARIATION
            }
            in VERTICAL_ANGLE_RANGE_MAX -> {
                viewHeight / 2 - currentPitch * viewHeight /
                        2 / MAX_VERTICAL_PITCH_VARIATION
            }
            else -> 0f
        }
    }

    fun adjustLowPassFilterAlphaValue(positionX: Float, viewWidth: Int): Float {
        val centerPosition = viewWidth / 2f
        return when (positionX) {
            in 0f..centerPosition -> calculateLowPassFilterAlphaBeforeCenter(
                centerPosition,
                positionX
            )
            in centerPosition..viewWidth.toFloat() -> calculateLowPassFilterAlphaAfterCenter(
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
        (LOW_PASS_FILTER_ALPHA_NORMAL - LOW_PASS_FILTER_ALPHA_PRECISE) /
                centerPosition * positionX + 2 * LOW_PASS_FILTER_ALPHA_PRECISE - LOW_PASS_FILTER_ALPHA_NORMAL

    private fun calculateLowPassFilterAlphaBeforeCenter(
        centerPosition: Float,
        positionX: Float
    ) =
        (LOW_PASS_FILTER_ALPHA_PRECISE - LOW_PASS_FILTER_ALPHA_NORMAL) /
                centerPosition * positionX + LOW_PASS_FILTER_ALPHA_NORMAL

    fun prepareLabelsProperties(
        compassData: CompassData, viewWidth: Int,
        viewHeight: Int
    ): List<ARLabelProperties> {

        return compassData.destinations
            .filter { shouldShowLabel(it.currentDestinationAzimuth) }
            .sortedByDescending { it.distanceToDestination }
            .map { destinationData ->
                ARLabelProperties(
                    destinationData.distanceToDestination,
                    calculatePositionX(destinationData.currentDestinationAzimuth, viewWidth),
                    calculatePositionY(compassData.orientationData.currentPitch, viewHeight),
                    getAlphaValue(
                        compassData.maxDistance,
                        compassData.minDistance,
                        destinationData.distanceToDestination
                    ),
                    id = destinationData.destinationLocation.hashCode()
                )
            }
    }

    private fun getAlphaValue(maxDistance: Int, minDistance: Int, distanceToDestination: Int): Int {
        return when (maxDistance) {
            minDistance -> MAX_ALPHA_VALUE.toInt()
            else -> (-ALPHA_DELTA / (maxDistance - minDistance) * distanceToDestination +
                    MAX_ALPHA_VALUE - ALPHA_DELTA + ALPHA_DELTA / (maxDistance - minDistance) * maxDistance
                    ).roundToInt()
        }
    }

    private fun shouldShowLabel(destinationAzimuth: Float) =
        (destinationAzimuth in HORIZONTAL_ANGLE_RANGE_MIN
                || destinationAzimuth in HORIZONTAL_ANGLE_RANGE_MAX)

    fun getShowUpAnimation(): ARLabelAnimationData {
        val propertySize = PropertyValuesHolder.ofInt(
            PROPERTY_SIZE, 0,
            ANIMATED_VALUE_MAX_SIZE
        )
        val arLabelAnimationData = ARLabelAnimationData(ValueAnimator().apply {
            setValues(propertySize)
            duration = ANIMATION_DURATION
            interpolator = AccelerateInterpolator(ACCELERATE_INTERPOLATOR_FACTOR)
            start()
        })

        arLabelAnimationData.valueAnimator?.addUpdateListener { animation ->
            arLabelAnimationData.animatedSize =
                animation.getAnimatedValue(PROPERTY_SIZE) as? Int ?: 0
        }

        return arLabelAnimationData
    }
}
