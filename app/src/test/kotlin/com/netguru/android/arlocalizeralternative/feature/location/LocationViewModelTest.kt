package com.netguru.android.arlocalizeralternative.feature.location

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.netguru.android.arlocalizeralternative.RxSchedulersOverrideRule
import com.netguru.android.arlocalizeralternative.feature.location.domain.AtmUseCase
import com.netguru.android.arlocalizeralternative.feature.location.domain.CurrentLocationUseCase
import com.netguru.android.arlocalizeralternative.feature.location.presentation.LocationViewModel
import com.netguru.android.arlocalizeralternative.feature.location.presentation.ViewMode
import com.netguru.arlocalizerview.location.LocationData
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class LocationViewModelTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val rxSchedulersOverrideRule = RxSchedulersOverrideRule()

    private val atmUseCase = mock<AtmUseCase>()
    private val currentLocationUseCase = mock<CurrentLocationUseCase>()
    private val latLngBounds = mock<LatLngBounds>()
    private val viewModeObserver: Observer<ViewMode> = mock()
    private val locationObserver: Observer<LatLng> = mock()
    private val destinationObserver: Observer<List<LocationData>> = mock()
    private val currentLocation = mock<LatLng>()
    private val destinations = listOf(
        LocationData(1.0, 1.0),
        LocationData(2.0, 2.0),
        LocationData(3.0, 3.0)
    )

    private lateinit var viewModel: LocationViewModel

    @Before
    fun init() {
        whenever(currentLocationUseCase.getCurrentLocation()).thenReturn(Single.just(currentLocation))
        viewModel =
            LocationViewModel(
                atmUseCase, currentLocationUseCase
            )
        viewModel.destinations.observeForever(destinationObserver)
        viewModel.viewMode.observeForever(viewModeObserver)
    }

    @Test
    fun `should get lastLocation when observing locationLiveData`() {
        viewModel.locationLiveData.observeForever(locationObserver)

        verify(currentLocationUseCase).getCurrentLocation()
        verify(locationObserver).onChanged(any())
    }

    @Test
    fun `should initialize viewMode with MapMode`() {
        verify(viewModeObserver).onChanged(ViewMode.MapMode)
    }

    @Test
    fun `should post MapViewMode on MapMode click`() {
        viewModel.mapModeClick()

        verify(viewModeObserver, times(2)).onChanged(ViewMode.MapMode)
    }

    @Test
    fun `should post ARViewMode on ARMode click`() {
        viewModel.arModeClick()

        verify(viewModeObserver).onChanged(ViewMode.ARMode)
    }

    @Test
    fun `should post destinations on showAtm`() {
        whenever(atmUseCase.getATM(latLngBounds)).thenReturn(Single.just(destinations))

        viewModel.showATMinTheArea(latLngBounds)

        verify(atmUseCase).getATM(latLngBounds)
        verify(destinationObserver).onChanged(destinations)
    }
}
