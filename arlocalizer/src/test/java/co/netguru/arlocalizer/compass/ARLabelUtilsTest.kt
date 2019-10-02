package co.netguru.arlocalizer.compass

import co.netguru.arlocalizer.arview.ARLabelUtils
import co.netguru.arlocalizer.location.LocationData
import co.netguru.arlocalizer.orientation.OrientationData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ARLabelUtilsTest {

    private val viewWidth = 100
    private val viewHeight = 100

    @Test
    fun `for maximum Angle range end positionX on the display should be in the center (half of width)`() {
        val currentDestinationAzimuth = ARLabelUtils.HORIZONTAL_ANGLE_RANGE_MAX.endInclusive

        val positionX = ARLabelUtils.calculatePositionX(currentDestinationAzimuth, viewWidth)

        assert(positionX.toInt() == viewWidth / 2)
    }

    @Test
    fun `for minimum Angle range start positionX on the display should be in the center (half of width)`() {
        val currentDestinationAzimuth = ARLabelUtils.HORIZONTAL_ANGLE_RANGE_MIN.start

        val positionX = ARLabelUtils.calculatePositionX(currentDestinationAzimuth, viewWidth)

        assert(positionX.toInt() == viewWidth / 2)
    }

    @Test
    fun `for minimum angle range end positionX should be reaching end of display`() {
        val currentDestinationAzimuth = ARLabelUtils.HORIZONTAL_ANGLE_RANGE_MIN.endInclusive

        val positionX = ARLabelUtils.calculatePositionX(currentDestinationAzimuth, viewWidth)

        assert(positionX.toInt() > viewWidth)
    }

    @Test
    fun `for maximum angle range start positionX should be reaching start of display`() {
        val currentDestinationAzimuth = ARLabelUtils.HORIZONTAL_ANGLE_RANGE_MAX.start

        val positionX = ARLabelUtils.calculatePositionX(currentDestinationAzimuth, viewWidth)

        assert(positionX.toInt() < 0)
    }

    @Test
    fun `low pass filter alpha should have the precise value at the center of the screen`() {
        val centerPosition = viewWidth / 2f

        val lowPassFilterAlpha =
            ARLabelUtils.adjustLowPassFilterAlphaValue(centerPosition, viewWidth)

        assert(lowPassFilterAlpha == ARLabelUtils.LOW_PASS_FILTER_ALPHA_PRECISE)
    }

    @Test
    fun `low pass filter alpha should have the normal value at the start of the screen`() {
        val startPosition = 0f

        val lowPassFilterAlpha =
            ARLabelUtils.adjustLowPassFilterAlphaValue(startPosition, viewWidth)

        assert(lowPassFilterAlpha == ARLabelUtils.LOW_PASS_FILTER_ALPHA_NORMAL)
    }

    @Test
    fun `low pass filter alpha should have the normal value at the end of the screen`() {
        val endPosition = viewWidth.toFloat()

        val lowPassFilterAlpha = ARLabelUtils.adjustLowPassFilterAlphaValue(endPosition, viewWidth)

        assert(lowPassFilterAlpha == ARLabelUtils.LOW_PASS_FILTER_ALPHA_NORMAL)
    }

    @Test
    fun `low pass filter alpha should be in range of normal and precise`() {
        val positionX = viewWidth / 3f

        val lowPassFilterAlpha = ARLabelUtils.adjustLowPassFilterAlphaValue(positionX, viewWidth)

        assert(
            lowPassFilterAlpha in
                    ARLabelUtils.LOW_PASS_FILTER_ALPHA_NORMAL
                    ..ARLabelUtils.LOW_PASS_FILTER_ALPHA_PRECISE
        )
    }

    @Test
    fun `for minimum angle range end positionY on the display should be in the center (half of height)`() {
        val currentPitch = ARLabelUtils.VERTICAL_ANGLE_RANGE_MIN.endInclusive

        val positionY = ARLabelUtils.calculatePositionY(currentPitch, viewHeight)

        assert(positionY.toInt() == viewHeight / 2)
    }

    @Test
    fun `for maximum angle range start positionY on the display should be in the center (half of height)`() {
        val currentPitch = ARLabelUtils.VERTICAL_ANGLE_RANGE_MAX.start

        val positionY = ARLabelUtils.calculatePositionY(currentPitch, viewHeight)

        assert(positionY.toInt() == viewHeight / 2)
    }

    @Test
    fun `for maximum angle range end positionY on the display should be at the top`() {
        val currentPitch = ARLabelUtils.VERTICAL_ANGLE_RANGE_MAX.endInclusive

        val positionY = ARLabelUtils.calculatePositionY(currentPitch, viewHeight)

        assert(positionY.toInt() == 0)
    }

    @Test
    fun `for minimum angle range start positionY on the display should be at the bottom`() {
        val currentPitch = ARLabelUtils.VERTICAL_ANGLE_RANGE_MIN.start

        val positionY = ARLabelUtils.calculatePositionY(currentPitch, viewHeight)

        assert(positionY.toInt() == viewHeight)
    }

    @Test
    fun `prepare and sort (descending) label properties by distance`() {
        val currentPitch = 10f
        val nearestDestination = DestinationData(15f, 12, LocationData(54.2, 18.5))
        val furthestDestination = DestinationData(15f, 20, LocationData(54.2, 18.5))
        val destinations = arrayListOf(nearestDestination, furthestDestination)
        val compassData = CompassData(
            OrientationData(0f, currentPitch), destinations,
            0, 0, LocationData
                (0.0, 0.0)
        )


        val labelProperties =
            ARLabelUtils.prepareLabelsProperties(compassData, viewWidth, viewHeight)

        val nearestDestinationExpectedIndex = 1
        val furthestDestinationExpectedIndex = 0
        assertNotEquals(destinations.indexOf(nearestDestination), nearestDestinationExpectedIndex)
        assertNotEquals(destinations.indexOf(furthestDestination), furthestDestinationExpectedIndex)
        assertEquals(
            nearestDestinationExpectedIndex,
            labelProperties.indexOfFirst { it.distance == nearestDestination.distanceToDestination })
        assertEquals(
            furthestDestinationExpectedIndex,
            labelProperties.indexOfFirst { it.distance == furthestDestination.distanceToDestination })
    }

    @Test
    fun `prepare and label properties with alpha set by distance`() {
        val minDistance = 12
        val maxDistance = 20
        val nearestDestination = DestinationData(0f, minDistance, LocationData(0.0, 0.0))
        val furthestDestination = DestinationData(0f, maxDistance, LocationData(0.0, 0.0))
        val destinations = arrayListOf(nearestDestination, furthestDestination)
        val compassData = CompassData(
            OrientationData(0f, 0f), destinations,
            maxDistance, minDistance, LocationData
                (0.0, 0.0)
        )

        val labelProperties =
            ARLabelUtils.prepareLabelsProperties(compassData, viewWidth, viewHeight)
        val maxAlphaValue = ARLabelUtils.MAX_ALPHA_VALUE.toInt()
        val minAlphaValue = (ARLabelUtils.MAX_ALPHA_VALUE - ARLabelUtils.ALPHA_DELTA).toInt()

        assertEquals(
            maxAlphaValue,
            labelProperties[1].alpha
        )
        assertEquals(
            minAlphaValue,
            labelProperties[0].alpha
        )
    }
}
