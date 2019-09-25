package co.netguru.arlocalizer.compass

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import co.netguru.arlocalizer.RxSchedulersOverrideRule
import io.reactivex.BackpressureStrategy
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import kotlin.math.roundToInt

class CompassRepositoryTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val schedulersRule = RxSchedulersOverrideRule()

    private lateinit var compassRepository: CompassRepository
    @Mock
    private lateinit var locationProvider: co.netguru.arlocalizer.location.LocationProvider
    @Mock
    private lateinit var orientationProvider: co.netguru.arlocalizer.orientation.OrientationProvider

    @Mock
    internal lateinit var observer: Observer<co.netguru.arlocalizer.common.Result<CompassData>>

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        compassRepository =
            CompassRepository(orientationProvider, locationProvider)
        compassRepository.compassStateLiveData.observeForever(observer)
    }

    private fun mockDefaultLocationUpdates(): Pair<PublishSubject<co.netguru.arlocalizer.location.LocationData>, co.netguru.arlocalizer.location.LocationData> {
        val publishSubject = PublishSubject.create<co.netguru.arlocalizer.location.LocationData>()
        val latitude = 50.0
        val longitude = 20.0
        val compassLocation = co.netguru.arlocalizer.location.LocationData(latitude, longitude)
        `when`(locationProvider.getLocationUpdates()).thenReturn(publishSubject.toFlowable(
            BackpressureStrategy.BUFFER))
        return publishSubject to compassLocation
    }

    private fun mockDefaultOrientationUpdates(): Pair<PublishSubject<co.netguru.arlocalizer.orientation.OrientationData>, co.netguru.arlocalizer.orientation.OrientationData> {
        val orientationSubject = PublishSubject.create<co.netguru.arlocalizer.orientation.OrientationData>()
        val currentAzimuth = 50f
        val azimuthData = co.netguru.arlocalizer.orientation.OrientationData(currentAzimuth, 0f)
        `when`(orientationProvider.getSensorUpdates()).thenReturn(orientationSubject.toFlowable(BackpressureStrategy.BUFFER))
        return orientationSubject to azimuthData
    }

    @Test
    fun `should get location updates after starting observation`() {
        val publishSubject = PublishSubject.create<co.netguru.arlocalizer.location.LocationData>()
        val latitude = 50.0
        val longitude = 20.0
        val compassLocation = co.netguru.arlocalizer.location.LocationData(latitude, longitude)
        `when`(locationProvider.getLocationUpdates()).thenReturn(publishSubject.toFlowable(
            BackpressureStrategy.BUFFER))
       val mockedOrientation = mockDefaultOrientationUpdates()

        compassRepository.startCompass()
        publishSubject.onNext(compassLocation)
        mockedOrientation.first.onNext(mockedOrientation.second)

        assert(compassRepository.compassStateLiveData.value is co.netguru.arlocalizer.common.Result.Success)
        assert((compassRepository.compassStateLiveData.value as co.netguru.arlocalizer.common.Result.Success).data.currentLocation == compassLocation)

        val latitude2 = 60.0
        val longitude2 = -20.0
        val compassLocation2 = co.netguru.arlocalizer.location.LocationData(latitude2, longitude2)

        publishSubject.onNext(compassLocation2)

        assert(compassRepository.compassStateLiveData.value is co.netguru.arlocalizer.common.Result.Success)
        assert((compassRepository.compassStateLiveData.value as co.netguru.arlocalizer.common.Result.Success).data.currentLocation == compassLocation2)
    }

    @Test
    fun `should not get location updates without starting observation`() {
        val publishSubject = PublishSubject.create<co.netguru.arlocalizer.location.LocationData>()
        val latitude = 50.0
        val longitude = 20.0
        val compassLocation = co.netguru.arlocalizer.location.LocationData(latitude, longitude)
        `when`(locationProvider.getLocationUpdates()).thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER))
        val mockedDefaultOrientationUpdates = mockDefaultOrientationUpdates()

        publishSubject.onNext(compassLocation)
        mockedDefaultOrientationUpdates.first.onNext(mockedDefaultOrientationUpdates.second)

        assert(compassRepository.compassStateLiveData.value == null)
    }

    @Test
    fun `should get orientation updates after starting observation`() {
        val publishSubject = PublishSubject.create<co.netguru.arlocalizer.orientation.OrientationData>()
        val currentAzimuth = 50f
        val azimuthData = co.netguru.arlocalizer.orientation.OrientationData(currentAzimuth, 0f)
        `when`(orientationProvider.getSensorUpdates()).thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER))
        val mockedDefaultLocationUpdates = mockDefaultLocationUpdates()


        compassRepository.startCompass()
        mockedDefaultLocationUpdates.first.onNext(mockedDefaultLocationUpdates.second)
        publishSubject.onNext(azimuthData)

        assert(compassRepository.compassStateLiveData.value is co.netguru.arlocalizer.common.Result.Success)
        assert((compassRepository.compassStateLiveData.value as co.netguru.arlocalizer.common.Result.Success).data.orientationData.currentAzimuth == currentAzimuth)

        val currentAzimuth2 = 30f
        val azimuthData2 = co.netguru.arlocalizer.orientation.OrientationData(currentAzimuth2, 0f)

        publishSubject.onNext(azimuthData2)

        assert(compassRepository.compassStateLiveData.value is co.netguru.arlocalizer.common.Result.Success)
        assert((compassRepository.compassStateLiveData.value as co.netguru.arlocalizer.common.Result.Success).data.orientationData.currentAzimuth == currentAzimuth2)
    }

    @Test
    fun `should not get orientation updates without starting observation`() {
        val publishSubject = PublishSubject.create<co.netguru.arlocalizer.orientation.OrientationData>()
        val currentAzimuth = 50f
        val azimuthData = co.netguru.arlocalizer.orientation.OrientationData(currentAzimuth, 0f)
        `when`(orientationProvider.getSensorUpdates()).thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER))

        publishSubject.onNext(azimuthData)

        assert(compassRepository.compassStateLiveData.value == null)
    }


    @Test
    fun `should stop location updates after stopping observation`() {
        val publishSubject = PublishSubject.create<co.netguru.arlocalizer.location.LocationData>()
        val latitude = 50.0
        val longitude = 20.0
        val compassLocation = co.netguru.arlocalizer.location.LocationData(latitude, longitude)
        `when`(locationProvider.getLocationUpdates()).thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER))
        val mockedDefaultOrientationUpdates = mockDefaultOrientationUpdates()

        compassRepository.startCompass()
        publishSubject.onNext(compassLocation)
        mockedDefaultOrientationUpdates.first.onNext(mockedDefaultOrientationUpdates.second)

        assert(compassRepository.compassStateLiveData.value is co.netguru.arlocalizer.common.Result.Success)
        assert((compassRepository.compassStateLiveData.value as co.netguru.arlocalizer.common.Result.Success).data.currentLocation == compassLocation)

        val latitude2 = 60.0
        val longitude2 = -20.0
        val compassLocation2 = co.netguru.arlocalizer.location.LocationData(latitude2, longitude2)

        compassRepository.stopCompass()
        publishSubject.onNext(compassLocation2)

        assert(compassRepository.compassStateLiveData.value is co.netguru.arlocalizer.common.Result.Success)
        assert((compassRepository.compassStateLiveData.value as co.netguru.arlocalizer.common.Result.Success).data.currentLocation != compassLocation2)
    }

    @Test
    fun `should stop orientation updates after stopping observation`() {
        val publishSubject = PublishSubject.create<co.netguru.arlocalizer.orientation.OrientationData>()
        val currentAzimuth = 50f
        val azimuthData = co.netguru.arlocalizer.orientation.OrientationData(currentAzimuth, 0f)
        `when`(orientationProvider.getSensorUpdates()).thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER))
        val mockedDefautLocationUpdates = mockDefaultLocationUpdates()

        compassRepository.startCompass()
        mockedDefautLocationUpdates.first.onNext(mockedDefautLocationUpdates.second)
        publishSubject.onNext(azimuthData)

        assert(compassRepository.compassStateLiveData.value is co.netguru.arlocalizer.common.Result.Success)
        assert((compassRepository.compassStateLiveData.value as co.netguru.arlocalizer.common.Result.Success).data.orientationData.currentAzimuth == currentAzimuth)

        val currentAzimuth2 = 30f
        val azimuthData2 = co.netguru.arlocalizer.orientation.OrientationData(currentAzimuth2, 0f)

        compassRepository.stopCompass()
        publishSubject.onNext(azimuthData2)

        assert(compassRepository.compassStateLiveData.value is co.netguru.arlocalizer.common.Result.Success)
        assert((compassRepository.compassStateLiveData.value as co.netguru.arlocalizer.common.Result.Success).data.orientationData.currentAzimuth != currentAzimuth2)
    }

    @Test
    fun `should calculate correct heading angle based on azimuth and location`() {
        val locationPublishSubject = PublishSubject.create<co.netguru.arlocalizer.location.LocationData>()
        var compassLocation =
            co.netguru.arlocalizer.location.LocationData(51.70255082465981, 19.82147216796875)
        `when`(locationProvider.getLocationUpdates()).thenReturn(locationPublishSubject.toFlowable(BackpressureStrategy.BUFFER))


        val orientationPublishSubject = PublishSubject.create<co.netguru.arlocalizer.orientation.OrientationData>()
        `when`(orientationProvider.getSensorUpdates()).thenReturn(orientationPublishSubject.toFlowable(BackpressureStrategy.BUFFER))
        compassRepository.destination =
            co.netguru.arlocalizer.location.LocationData(51.782355138660385, 20.156112670898438)
        compassRepository.startCompass()
        locationPublishSubject.onNext(compassLocation)

        //1
        var azimuthData = co.netguru.arlocalizer.orientation.OrientationData(50f, 0f)
        orientationPublishSubject.onNext(azimuthData)

        var success = (compassRepository.compassStateLiveData.value as co.netguru.arlocalizer.common.Result.Success)
        org.junit.Assert.assertEquals(19, success.data.currentDestinationAzimuth.roundToInt())

        //2
        azimuthData = co.netguru.arlocalizer.orientation.OrientationData(320f, 0f)
        orientationPublishSubject.onNext(azimuthData)

        success = (compassRepository.compassStateLiveData.value as co.netguru.arlocalizer.common.Result.Success)
        org.junit.Assert.assertEquals(109, success.data.currentDestinationAzimuth.roundToInt())

        //3
        azimuthData = co.netguru.arlocalizer.orientation.OrientationData(0f, 0f)
        orientationPublishSubject.onNext(azimuthData)

        success = (compassRepository.compassStateLiveData.value as co.netguru.arlocalizer.common.Result.Success)
        org.junit.Assert.assertEquals(69, success.data.currentDestinationAzimuth.roundToInt())

        //4
        azimuthData = co.netguru.arlocalizer.orientation.OrientationData(360f, 0f)
        orientationPublishSubject.onNext(azimuthData)

        success = (compassRepository.compassStateLiveData.value as co.netguru.arlocalizer.common.Result.Success)
        org.junit.Assert.assertEquals(69, success.data.currentDestinationAzimuth.roundToInt())


        //5
        azimuthData = co.netguru.arlocalizer.orientation.OrientationData(160f, 0f)
        orientationPublishSubject.onNext(azimuthData)

        success = (compassRepository.compassStateLiveData.value as co.netguru.arlocalizer.common.Result.Success)
        org.junit.Assert.assertEquals(269, success.data.currentDestinationAzimuth.roundToInt())


        // II
        compassLocation =
            co.netguru.arlocalizer.location.LocationData(51.782355138660385, 20.156112670898438)
        locationPublishSubject.onNext(compassLocation)
        compassRepository.destination =
            co.netguru.arlocalizer.location.LocationData(51.70255082465981, 19.82147216796875)

        //1
        azimuthData = co.netguru.arlocalizer.orientation.OrientationData(50f, 0f)
        orientationPublishSubject.onNext(azimuthData)

        success = (compassRepository.compassStateLiveData.value as co.netguru.arlocalizer.common.Result.Success)
        org.junit.Assert.assertEquals(199, success.data.currentDestinationAzimuth.roundToInt())

        //2
        azimuthData = co.netguru.arlocalizer.orientation.OrientationData(320f, 0f)
        orientationPublishSubject.onNext(azimuthData)

        success = (compassRepository.compassStateLiveData.value as co.netguru.arlocalizer.common.Result.Success)
        org.junit.Assert.assertEquals(289, success.data.currentDestinationAzimuth.roundToInt())

        //3
        azimuthData = co.netguru.arlocalizer.orientation.OrientationData(0f, 0f)
        orientationPublishSubject.onNext(azimuthData)

        success = (compassRepository.compassStateLiveData.value as co.netguru.arlocalizer.common.Result.Success)
        org.junit.Assert.assertEquals(249, success.data.currentDestinationAzimuth.roundToInt())

        //4
        azimuthData = co.netguru.arlocalizer.orientation.OrientationData(360f, 0f)
        orientationPublishSubject.onNext(azimuthData)

        success = (compassRepository.compassStateLiveData.value as co.netguru.arlocalizer.common.Result.Success)
        org.junit.Assert.assertEquals(249, success.data.currentDestinationAzimuth.roundToInt())


        //5
        azimuthData = co.netguru.arlocalizer.orientation.OrientationData(160f, 0f)
        orientationPublishSubject.onNext(azimuthData)

        success = (compassRepository.compassStateLiveData.value as co.netguru.arlocalizer.common.Result.Success)
        org.junit.Assert.assertEquals(89, success.data.currentDestinationAzimuth.roundToInt())
    }
}