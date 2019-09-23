package co.netguru.arlocalizer.compass

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import co.netguru.arlocalizer.arview.ARLocalizerViewModel
import co.netguru.arlocalizer.common.Result
import co.netguru.arlocalizer.common.ViewState
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations


/**
 * Created by Mateusz on 11.04.2019.
 */

class CompassViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ARLocalizerViewModel

    @Mock
    internal lateinit var compassRepository: CompassRepository
    @Mock
    internal lateinit var permissionManager: co.netguru.arlocalizer.PermissionManager
    @Mock
    internal lateinit var observer: Observer<ViewState<CompassData>>

    private val compassStateLiveData = MutableLiveData<Result<CompassData>>()
    private val compassData = CompassData()
    private val throwable = Throwable()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        `when`(compassRepository.compassStateLiveData).thenReturn(compassStateLiveData)
        viewModel = ARLocalizerViewModel(
            compassRepository,
            permissionManager
        )
        viewModel.compassState.observeForever(observer)
    }

    @Test
    fun `should return viewState success on success result`() {
        compassStateLiveData.value = Result.Success(compassData)

        assert(viewModel.compassState.value is ViewState.Success)
    }

    @Test
    fun `should return viewState error on error result`() {
        compassStateLiveData.value = Result.Error(throwable)

        assert(viewModel.compassState.value is ViewState.Error)
    }

    @Test
    fun `should stop location and sensor observation on error result`() {
        compassStateLiveData.value = Result.Error(throwable)

        verify(compassRepository).stopCompass()
    }
}