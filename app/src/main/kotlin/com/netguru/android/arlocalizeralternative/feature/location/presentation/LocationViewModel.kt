package com.netguru.android.arlocalizeralternative.feature.location.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.toLiveData
import com.google.android.gms.maps.model.LatLngBounds
import com.netguru.android.arlocalizeralternative.feature.location.domain.AtmUseCase
import com.netguru.android.arlocalizeralternative.feature.location.domain.CurrentLocationUseCase
import com.netguru.arlocalizerview.location.LocationData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LocationViewModel @Inject constructor(
    private val atmUseCase: AtmUseCase,
    currentLocationUseCase: CurrentLocationUseCase
) :
    ViewModel() {

    val locationLiveData = currentLocationUseCase.getCurrentLocation().toFlowable().toLiveData()
    private val mutableDestinations = MutableLiveData<List<LocationData>>()
    val destinations: LiveData<List<LocationData>> = mutableDestinations
    private val mutableViewMode = MutableLiveData<ViewMode>(ViewMode.MapMode)
    val viewMode: LiveData<ViewMode> = mutableViewMode
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val mutableLoading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = mutableLoading


    private fun handleDestinations(destinations: List<LocationData>) {
        mutableDestinations.postValue(destinations)
    }

    fun arModeClick() {
        mutableViewMode.postValue(ViewMode.ARMode)
    }

    fun mapModeClick() {
        mutableViewMode.postValue(ViewMode.MapMode)
    }

    fun showATMinTheArea(latLngBounds: LatLngBounds) {
        mutableLoading.postValue(true)
        atmUseCase
            .getATM(latLngBounds)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onSuccess = {
                mutableLoading.postValue(false)
                handleDestinations(it)
            }
                , onError = {
                    mutableLoading.postValue(false)
                    it.printStackTrace()

                })
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
