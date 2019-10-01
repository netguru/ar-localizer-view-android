package co.netguru.arlocalizer.compass

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import co.netguru.arlocalizer.PermissionManager
import co.netguru.arlocalizer.PermissionResult
import co.netguru.arlocalizer.arview.ARLocalizerViewModel
import co.netguru.arlocalizer.common.ViewState
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Flowable
import org.junit.Before
import org.junit.Rule
import org.junit.Test


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

    private val throwable = Throwable()

    @Before
    fun setup() {
        viewModel = ARLocalizerViewModel(
            compassRepository,
            permissionManager
        )
        viewModel.compassState.observeForever(observer)

    }

    @Test
    fun `should return viewState success on success result`() {
        whenever(permissionManager.areAllPermissionsGranted()).thenReturn(true)
        whenever(compassRepository.getCompassUpdates()).thenReturn(Flowable.just(compassData))

        viewModel.startCompass()

        assert(viewModel.compassState.value is ViewState.Success)
    }

    @Test
    fun `should return viewState error on error result`() {
        whenever(permissionManager.areAllPermissionsGranted()).thenReturn(true)
        whenever(compassRepository.getCompassUpdates()).thenReturn(Flowable.error(throwable))

        viewModel.startCompass()

        assert(viewModel.compassState.value is ViewState.Error)
    }

    @Test
    fun `should not start updates when all permissions are not granted`() {
        whenever(permissionManager.areAllPermissionsGranted()).thenReturn(false)
        whenever(compassRepository.getCompassUpdates()).thenReturn(Flowable.just(compassData))

        viewModel.startCompass()

        assert(viewModel.compassState.value == null)
    }

    @Test
    fun `should start updates when all permissions are granted`() {
        whenever(permissionManager.areAllPermissionsGranted()).thenReturn(true)
        whenever(compassRepository.getCompassUpdates()).thenReturn(Flowable.just(compassData))

        viewModel.startCompass()

        assert(viewModel.compassState.value != null)
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

    @Test
    fun `should get compass updated on successful permission check`() {
        val requestCode = 0
        val permissions: Array<out String> = arrayOf()
        val grantResults: IntArray = intArrayOf()
        whenever(compassRepository.getCompassUpdates()).thenReturn(Flowable.just(compassData))
        whenever(permissionManager.getPermissionsRequestResult(requestCode, grantResults)).thenReturn(PermissionResult.GRANTED)
        whenever(permissionManager.areAllPermissionsGranted()).thenReturn(true)

        viewModel.onRequestPermissionResult(requestCode, permissions, grantResults)

        verify(compassRepository).getCompassUpdates()
    }
}
