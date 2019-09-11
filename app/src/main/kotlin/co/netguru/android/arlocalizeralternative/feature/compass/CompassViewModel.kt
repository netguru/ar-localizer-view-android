package co.netguru.android.arlocalizeralternative.feature.compass

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import co.netguru.android.arlocalizeralternative.common.Result
import co.netguru.android.arlocalizeralternative.common.ViewState
import co.netguru.android.arlocalizeralternative.feature.location.LocationData
import javax.inject.Inject

class CompassViewModel @Inject constructor(
    private val compassRepository: CompassRepository
) : ViewModel() {

    val viewState: LiveData<ViewState<CompassData>> =
        Transformations.map(compassRepository.compassStateLiveData) { compassDataResult: Result<CompassData> ->
            when (compassDataResult) {
                is Result.Success -> ViewState.Success(compassDataResult.data)
                is Result.Error -> {
                    stopSensorObservation()
                    stopLocationObservation()
                    ViewState.Error<CompassData>(compassDataResult.throwable.message ?: "Unexpected error")
                }
            }
        }

    fun setDestination(destination: LocationData) {
        compassRepository.destination = destination
    }

    fun startSensorObservation() {
        compassRepository.startSensorObservation()
    }

    fun stopSensorObservation() {
        compassRepository.stopSensorObservation()
    }

    fun startLocationObservation() {
        compassRepository.startLocationObservation()
    }

    fun stopLocationObservation() {
        compassRepository.stopLocationObservation()
    }

    fun setLowPassFilterAlpha(lowPassFilterAlpha: Float) {
        compassRepository.setLowPassFilterAlpha(lowPassFilterAlpha)
    }
}
