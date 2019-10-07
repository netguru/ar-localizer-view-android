package com.netguru.android.arlocalizeralternative.feature.location

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.netguru.arlocalizerview.location.LocationData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Task
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class LocationViewModelTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val fusedLocationProviderClient = mock<FusedLocationProviderClient>()
    private val locationTask = mock<Task<Location>>()
    private val viewModeObserver: Observer<ViewMode> = mock()
    private val destinationObserver: Observer<List<LocationData>> = mock()
    private val destinations = listOf(
        LocationData(1.0, 1.0),
        LocationData(2.0, 2.0),
        LocationData(3.0, 3.0)
    )

    private lateinit var viewModel: LocationViewModel

    @Before
    fun init() {
        viewModel = LocationViewModel(fusedLocationProviderClient)
        whenever(fusedLocationProviderClient.lastLocation).thenReturn(locationTask)
        viewModel.destinations.observeForever(destinationObserver)
        viewModel.viewMode.observeForever(viewModeObserver)
    }

    @Test
    fun `should get lastLocation on onMapReady`() {
        viewModel.onMapReady()

        verify(fusedLocationProviderClient).lastLocation
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
    fun `should post destinations on handleDestinations`() {
        viewModel.handleDestinations(destinations)

        verify(destinationObserver).onChanged(destinations)
    }
}
