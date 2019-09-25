package co.netguru.arlocalizer.compass

import org.junit.Test

class ArLabelPositionUtilsTest {

    private val displayWidth = 100
    private val displayHeight = 100

    @Test
    fun `for maximum Angle range end positionX on the display should be in the center (half of width)`() {
        val compassData = CompassData().apply {
            currentDestinationAzimuth = co.netguru.arlocalizer.arview.ARLabelPositionUtils.HORIZONTAL_ANGLE_RANGE_MAX.endInclusive
        }

        val positionX = co.netguru.arlocalizer.arview.ARLabelPositionUtils.calculatePositionX(compassData, displayWidth)

        assert(positionX.toInt() == displayWidth / 2)
    }

    @Test
    fun `for minimum Angle range start positionX on the display should be in the center (half of width)`() {
        val compassData = CompassData().apply {
            currentDestinationAzimuth = co.netguru.arlocalizer.arview.ARLabelPositionUtils.HORIZONTAL_ANGLE_RANGE_MIN.start
        }

        val positionX = co.netguru.arlocalizer.arview.ARLabelPositionUtils.calculatePositionX(compassData, displayWidth)

        assert(positionX.toInt() == displayWidth / 2)
    }

    @Test
    fun `for minimum angle range end positionX should be reaching end of display`() {
        val compassData = CompassData().apply {
            currentDestinationAzimuth = co.netguru.arlocalizer.arview.ARLabelPositionUtils.HORIZONTAL_ANGLE_RANGE_MIN.endInclusive
        }

        val positionX = co.netguru.arlocalizer.arview.ARLabelPositionUtils.calculatePositionX(compassData, displayWidth)

        assert(positionX.toInt() > displayWidth)
    }

    @Test
    fun `for maximum angle range start positionX should be reaching start of display`() {
        val compassData = CompassData().apply {
            currentDestinationAzimuth = co.netguru.arlocalizer.arview.ARLabelPositionUtils.HORIZONTAL_ANGLE_RANGE_MAX.start
        }

        val positionX = co.netguru.arlocalizer.arview.ARLabelPositionUtils.calculatePositionX(compassData, displayWidth)

        assert(positionX.toInt() < 0)
    }

    @Test
    fun `low pass filter alpha should have the precise value at the center of the screen`() {
        val centerPosition = displayWidth / 2f

        val lowPassFilterAlpha = co.netguru.arlocalizer.arview.ARLabelPositionUtils.adjustLowPassFilterAlphaValue(centerPosition, displayWidth)

        assert(lowPassFilterAlpha == co.netguru.arlocalizer.arview.ARLabelPositionUtils.LOW_PASS_FILTER_ALPHA_PRECISE)
    }

    @Test
    fun `low pass filter alpha should have the normal value at the start of the screen`() {
        val startPosition = 0f

        val lowPassFilterAlpha = co.netguru.arlocalizer.arview.ARLabelPositionUtils.adjustLowPassFilterAlphaValue(startPosition, displayWidth)

        assert(lowPassFilterAlpha == co.netguru.arlocalizer.arview.ARLabelPositionUtils.LOW_PASS_FILTER_ALPHA_NORMAL)
    }

    @Test
    fun `low pass filter alpha should have the normal value at the end of the screen`() {
        val endPosition = displayWidth.toFloat()

        val lowPassFilterAlpha = co.netguru.arlocalizer.arview.ARLabelPositionUtils.adjustLowPassFilterAlphaValue(endPosition, displayWidth)

        assert(lowPassFilterAlpha == co.netguru.arlocalizer.arview.ARLabelPositionUtils.LOW_PASS_FILTER_ALPHA_NORMAL)
    }

    @Test
    fun `low pass filter alpha should be in range of normal and precise`() {
        val positionX = displayWidth / 3f

        val lowPassFilterAlpha = co.netguru.arlocalizer.arview.ARLabelPositionUtils.adjustLowPassFilterAlphaValue(positionX, displayWidth)

        assert(lowPassFilterAlpha in co.netguru.arlocalizer.arview.ARLabelPositionUtils.LOW_PASS_FILTER_ALPHA_NORMAL..co.netguru.arlocalizer.arview.ARLabelPositionUtils.LOW_PASS_FILTER_ALPHA_PRECISE)
    }

    @Test
    fun `for minimum angle range end positionY on the display should be in the center (half of height)`() {
        val compassData = CompassData().apply {
            orientationData = co.netguru.arlocalizer.orientation.OrientationData(
                0f,
                co.netguru.arlocalizer.arview.ARLabelPositionUtils.VERTICAL_ANGLE_RANGE_MIN.endInclusive
            )
        }

        val positionY = co.netguru.arlocalizer.arview.ARLabelPositionUtils.calculatePositionY(compassData, displayHeight)

        assert(positionY.toInt() == displayHeight / 2)
    }

    @Test
    fun `for maximum angle range start positionY on the display should be in the center (half of height)`() {
        val compassData = CompassData().apply {
            orientationData = co.netguru.arlocalizer.orientation.OrientationData(
                0f,
                co.netguru.arlocalizer.arview.ARLabelPositionUtils.VERTICAL_ANGLE_RANGE_MAX.start
            )
        }

        val positionY = co.netguru.arlocalizer.arview.ARLabelPositionUtils.calculatePositionY(compassData, displayHeight)

        assert(positionY.toInt() == displayHeight / 2)
    }

    @Test
    fun `for maximum angle range end positionY on the display should be at the top`() {
        val compassData = CompassData().apply {
            orientationData = co.netguru.arlocalizer.orientation.OrientationData(
                0f,
                co.netguru.arlocalizer.arview.ARLabelPositionUtils.VERTICAL_ANGLE_RANGE_MAX.endInclusive
            )
        }

        val positionY = co.netguru.arlocalizer.arview.ARLabelPositionUtils.calculatePositionY(compassData, displayHeight)

        assert(positionY.toInt() == 0)
    }

    @Test
    fun `for minimum angle range start positionY on the display should be at the bottom`() {
        val compassData = CompassData().apply {
            orientationData = co.netguru.arlocalizer.orientation.OrientationData(
                0f,
                co.netguru.arlocalizer.arview.ARLabelPositionUtils.VERTICAL_ANGLE_RANGE_MIN.start
            )
        }

        val positionY = co.netguru.arlocalizer.arview.ARLabelPositionUtils.calculatePositionY(compassData, displayHeight)

        assert(positionY.toInt() == displayHeight)
    }
}
