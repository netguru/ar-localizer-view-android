package co.netguru.arlocalizer.compass

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import co.netguru.arlocalizer.RxSchedulersOverrideRule
import co.netguru.arlocalizer.location.LocationData
import co.netguru.arlocalizer.location.LocationProvider
import co.netguru.arlocalizer.orientation.OrientationData
import co.netguru.arlocalizer.orientation.OrientationProvider
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
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
    private val locationProvider: LocationProvider = mock()
    private val orientationProvider: OrientationProvider = mock()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        compassRepository =
            CompassRepository(orientationProvider, locationProvider)
    }

    @Test
    fun `should get compassData updates after starting observation`() {
        val locationData = LocationData(50.0, 20.0)
        val orientationData = OrientationData(50f, 20f)
        whenever(locationProvider.getLocationUpdates()).thenReturn(
            Flowable.just(locationData)
        )
        whenever(orientationProvider.getSensorUpdates()).thenReturn(
            Flowable.just(orientationData)
        )
        val testSubscriber = compassRepository.getCompassUpdates().test()

        testSubscriber
            .assertValueAt(0) { compassData -> compassData.currentLocation == locationData }
            .assertValueAt(0) { compassData -> compassData.orientationData == orientationData }
    }

    @Test
    fun `should calculate correct heading angle based on azimuth and location`() {
        val locationPublishSubject = PublishSubject.create<LocationData>()
        var compassLocation = LocationData(51.70255082465981, 19.82147216796875)
        whenever(locationProvider.getLocationUpdates()).thenReturn(
            locationPublishSubject.toFlowable(
                BackpressureStrategy.BUFFER
            )
        )

        val orientationPublishSubject = PublishSubject.create<OrientationData>()
        whenever(orientationProvider.getSensorUpdates())
            .thenReturn(orientationPublishSubject.toFlowable(BackpressureStrategy.BUFFER))
        compassRepository.destinationsLocation = listOf(
            LocationData(
                51.782355138660385,
                20.156112670898438
            )
        )
        val testSubscriber = compassRepository.getCompassUpdates().test()
        locationPublishSubject.onNext(compassLocation)

        //1
        var azimuthData = OrientationData(50f, 0f)
        orientationPublishSubject.onNext(azimuthData)

        //2
        azimuthData = OrientationData(320f, 0f)
        orientationPublishSubject.onNext(azimuthData)

        //3
        azimuthData = OrientationData(0f, 0f)
        orientationPublishSubject.onNext(azimuthData)

        //4
        azimuthData = OrientationData(360f, 0f)
        orientationPublishSubject.onNext(azimuthData)

        //5
        azimuthData = OrientationData(160f, 0f)
        orientationPublishSubject.onNext(azimuthData)

        // II
        compassLocation =
            LocationData(51.782355138660385, 20.156112670898438)
        locationPublishSubject.onNext(compassLocation)
        compassRepository.destinationsLocation =
            listOf(LocationData(51.70255082465981, 19.82147216796875))

        //1
        azimuthData = OrientationData(50f, 0f)
        orientationPublishSubject.onNext(azimuthData)

        //2
        azimuthData = OrientationData(320f, 0f)
        orientationPublishSubject.onNext(azimuthData)

        //3
        azimuthData = OrientationData(0f, 0f)
        orientationPublishSubject.onNext(azimuthData)

        //4
        azimuthData = OrientationData(360f, 0f)
        orientationPublishSubject.onNext(azimuthData)

        //5
        azimuthData = OrientationData(160f, 0f)
        orientationPublishSubject.onNext(azimuthData)

        testSubscriber
            .assertValueAt(0) { it.destinations[0].currentDestinationAzimuth.roundToInt() == 19 }
            .assertValueAt(1) { it.destinations[0].currentDestinationAzimuth.roundToInt() == 109 }
            .assertValueAt(2) { it.destinations[0].currentDestinationAzimuth.roundToInt() == 69 }
            .assertValueAt(3) { it.destinations[0].currentDestinationAzimuth.roundToInt() == 69 }
            .assertValueAt(4) { it.destinations[0].currentDestinationAzimuth.roundToInt() == 269 }
            .assertValueAt(6) { it.destinations[0].currentDestinationAzimuth.roundToInt() == 199 }
            .assertValueAt(7) { it.destinations[0].currentDestinationAzimuth.roundToInt() == 289 }
            .assertValueAt(8) { it.destinations[0].currentDestinationAzimuth.roundToInt() == 249 }
            .assertValueAt(9) { it.destinations[0].currentDestinationAzimuth.roundToInt() == 249 }
            .assertValueAt(10) { it.destinations[0].currentDestinationAzimuth.roundToInt() == 89 }
    }
}
