package co.netguru.android.arlocalizeralternative.feature.compass

import co.netguru.android.arlocalizeralternative.feature.camera.ARLabelPositionUtils
import co.netguru.android.arlocalizeralternative.feature.orientation.OrientationData
import org.junit.Test

class ArLabelPositionUtilsTest {

    private val displayWidth = 100
    private val displayHeight = 100

    @Test
    fun `for maximum Angle range end positionX on the display should be in the center (half of width)`() {
        val compassData = CompassData().apply {
            currentDestinationAzimuth = ARLabelPositionUtils.HORIZONTAL_ANGLE_RANGE_MAX.endInclusive
        }

        val positionX = ARLabelPositionUtils.calculatePositionX(compassData, displayWidth)

        assert(positionX.toInt() == displayWidth / 2)
    }

    @Test
    fun `for minimum Angle range start positionX on the display should be in the center (half of width)`() {
        val compassData = CompassData().apply {
            currentDestinationAzimuth = ARLabelPositionUtils.HORIZONTAL_ANGLE_RANGE_MIN.start
        }

        val positionX = ARLabelPositionUtils.calculatePositionX(compassData, displayWidth)

        assert(positionX.toInt() == displayWidth / 2)
    }

    @Test
    fun `for minimum angle range end positionX should be reaching end of display`() {
        val compassData = CompassData().apply {
            currentDestinationAzimuth = ARLabelPositionUtils.HORIZONTAL_ANGLE_RANGE_MIN.endInclusive
        }

        val positionX = ARLabelPositionUtils.calculatePositionX(compassData, displayWidth)

        assert(positionX.toInt() > displayWidth)
    }

    @Test
    fun `for maximum angle range start positionX should be reaching start of display`() {
        val compassData = CompassData().apply {
            currentDestinationAzimuth = ARLabelPositionUtils.HORIZONTAL_ANGLE_RANGE_MAX.start
        }

        val positionX = ARLabelPositionUtils.calculatePositionX(compassData, displayWidth)

        assert(positionX.toInt() < 0)
    }

    @Test
    fun `low pass filter alpha should have the precise value at the center of the screen`() {
        val centerPosition = displayWidth / 2f

        val lowPassFilterAlpha = ARLabelPositionUtils.adjustLowPassFilterAlphaValue(centerPosition, displayWidth)

        assert(lowPassFilterAlpha == ARLabelPositionUtils.LOW_PASS_FILTER_ALPHA_PRECISE)
    }

    @Test
    fun `low pass filter alpha should have the normal value at the start of the screen`() {
        val startPosition = 0f

        val lowPassFilterAlpha = ARLabelPositionUtils.adjustLowPassFilterAlphaValue(startPosition, displayWidth)

        assert(lowPassFilterAlpha == ARLabelPositionUtils.LOW_PASS_FILTER_ALPHA_NORMAL)
    }

    @Test
    fun `low pass filter alpha should have the normal value at the end of the screen`() {
        val endPosition = displayWidth.toFloat()

        val lowPassFilterAlpha = ARLabelPositionUtils.adjustLowPassFilterAlphaValue(endPosition, displayWidth)

        assert(lowPassFilterAlpha == ARLabelPositionUtils.LOW_PASS_FILTER_ALPHA_NORMAL)
    }

    @Test
    fun `low pass filter alpha should be in range of normal and precise`() {
        val positionX = displayWidth / 3f

        val lowPassFilterAlpha = ARLabelPositionUtils.adjustLowPassFilterAlphaValue(positionX, displayWidth)

        assert(lowPassFilterAlpha in ARLabelPositionUtils.LOW_PASS_FILTER_ALPHA_NORMAL..ARLabelPositionUtils.LOW_PASS_FILTER_ALPHA_PRECISE)
    }

    @Test
    fun `for minimum angle range end positionY on the display should be in the center (half of height)`() {
        val compassData = CompassData().apply {
            orientationData = OrientationData(0f, ARLabelPositionUtils.VERTICAL_ANGLE_RANGE_MIN.endInclusive)
        }

        val positionY = ARLabelPositionUtils.calculatePositionY(compassData, displayHeight)

        assert(positionY.toInt() == displayHeight / 2)
    }

    @Test
    fun `for maximum angle range start positionY on the display should be in the center (half of height)`() {
        val compassData = CompassData().apply {
            orientationData = OrientationData(0f, ARLabelPositionUtils.VERTICAL_ANGLE_RANGE_MAX.start)
        }

        val positionY = ARLabelPositionUtils.calculatePositionY(compassData, displayHeight)

        assert(positionY.toInt() == displayHeight / 2)
    }

    @Test
    fun `for maximum angle range end positionY on the display should be at the top`() {
        val compassData = CompassData().apply {
            orientationData = OrientationData(0f, ARLabelPositionUtils.VERTICAL_ANGLE_RANGE_MAX.endInclusive)
        }

        val positionY = ARLabelPositionUtils.calculatePositionY(compassData, displayHeight)

        assert(positionY.toInt() == 0)
    }

    @Test
    fun `for minimum angle range start positionY on the display should be at the bottom`() {
        val compassData = CompassData().apply {
            orientationData = OrientationData(0f, ARLabelPositionUtils.VERTICAL_ANGLE_RANGE_MIN.start)
        }

        val positionY = ARLabelPositionUtils.calculatePositionY(compassData, displayHeight)

        assert(positionY.toInt() == displayHeight)
    }
}