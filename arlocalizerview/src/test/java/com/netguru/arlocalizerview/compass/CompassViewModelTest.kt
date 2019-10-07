package com.netguru.arlocalizerview.compass

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.netguru.arlocalizerview.PermissionManager
import com.netguru.arlocalizerview.PermissionResult
import com.netguru.arlocalizerview.arview.ARLocalizerViewModel
import com.netguru.arlocalizerview.common.ViewState
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Flowable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor


/**
 * Created by Mateusz on 11.04.2019.
 */

class CompassViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ARLocalizerViewModel

    private val compassRepository: CompassRepository = mock()
    private val permissionManager: PermissionManager = mock()
    private val observer: Observer<ViewState<CompassData>> = mock()
    private val compassData: CompassData = mock()
    private val argumentCaptor = ArgumentCaptor.forClass(ViewState::class.java)

    private val throwable = Throwable()

    @Before
    fun setup() {
        viewModel = ARLocalizerViewModel(
            compassRepository,
            permissionManager
        )
    }

    @Test
    fun `should return viewState success on success result`() {
        whenever(permissionManager.areAllPermissionsGranted()).thenReturn(true)
        whenever(compassRepository.getCompassUpdates()).thenReturn(Flowable.just(compassData))

        viewModel.compassState().observeForever(observer)

        verify(observer).onChanged(argumentCaptor.capture() as ViewState<CompassData>?)
        assert(argumentCaptor.allValues[0] is ViewState.Success)
    }

    @Test
    fun `should return viewState error on error result`() {
        whenever(permissionManager.areAllPermissionsGranted()).thenReturn(true)
        whenever(compassRepository.getCompassUpdates()).thenReturn(Flowable.error(throwable))

        viewModel.compassState().observeForever(observer)

        verify(observer).onChanged(argumentCaptor.capture() as ViewState<CompassData>?)
        assert(argumentCaptor.allValues[0] is ViewState.Error)
    }

    @Test
    fun `should request permissions on permissionsCheck when they are not granted`() {
        whenever(permissionManager.areAllPermissionsGranted()).thenReturn(false)

        viewModel.checkPermissions()

        verify(permissionManager).requestAllPermissions()
    }

    @Test
    fun `should not request permissions on permissionsCheck when they are granted`() {
        whenever(permissionManager.areAllPermissionsGranted()).thenReturn(true)

        viewModel.checkPermissions()

        verify(permissionManager, times(0)).requestAllPermissions()
        assert(viewModel.permissionState.value == PermissionResult.GRANTED)
    }
}
