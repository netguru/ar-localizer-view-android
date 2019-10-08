package com.netguru.android.arlocalizeralternative.feature.location.presentation

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.animation.doOnStart
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.netguru.android.arlocalizeralternative.R
import com.netguru.android.arlocalizeralternative.common.base.BaseActivity
import com.netguru.arlocalizerview.ARLocalizerDependencyProvider
import com.netguru.arlocalizerview.location.LocationData
import kotlinx.android.synthetic.main.activity_location.*
import javax.inject.Inject


open class LocationActivity : BaseActivity(), ARLocalizerDependencyProvider {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val locationViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get<LocationViewModel>()
    }

    private lateinit var googleMap: GoogleMap
    private val markers: MutableList<Marker> = mutableListOf()
    private var changeModeTransitionAnimation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_location)

        arLocalizer.onCreate(this)
        mapView.addToLifecycle(lifecycle, savedInstanceState)
        setupButtons()
        requestLocationPermission()
    }

    private fun setupButtons() {
        arViewIcon.setOnClickListener {
            changeModeTransitionAnimation = true
            locationViewModel.arModeClick()
        }

        backToMapButton.setOnClickListener {
            changeModeTransitionAnimation = true
            locationViewModel.mapModeClick()
        }

        findAtmButton.setOnClickListener {
            locationViewModel.showATMinTheArea(googleMap.projection.visibleRegion.latLngBounds)
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        arLocalizer.onRequestPermissionResult(requestCode, permissions, grantResults)
        val locationPermissionIndex = permissions.indexOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (locationPermissionIndex != -1
            && grantResults[locationPermissionIndex] == PackageManager.PERMISSION_GRANTED) {
            initMap()
        }
    }

    private fun initMap() {
        mapView.getMapAsync { googleMap ->
            this.googleMap = googleMap
            setupObservers()
            styleMap(googleMap)
        }
    }

    private fun setupObservers() {
        locationViewModel.locationLiveData.observe(this, Observer { location ->
            moveToLocation(location)
        })
        locationViewModel.destinations.observe(this, Observer { destinations ->
            arLocalizer.setDestinations(destinations)
            showDestinationsOnMap(destinations)
        })
        locationViewModel.viewMode.observe(this, Observer { viewMode ->
            when (viewMode) {
                ViewMode.ARMode -> handleArMode()
                ViewMode.MapMode -> handleMapMode()
            }
        })
        locationViewModel.loading.observe(this, Observer {
            googleMap.uiSettings.isScrollGesturesEnabled = !it
            loadingProgress.isVisible = it
        })
    }

    private fun showDestinationsOnMap(destinations: List<LocationData>) {
        if (markers.isNotEmpty()) removeMarkers()
        val latLngDestinations = destinations
            .map { LatLng(it.latitude, it.longitude) }

        latLngDestinations
            .forEach {
                addMarker(it)
            }
    }

    private fun removeMarkers() {
        markers.forEach {
            it.remove()
        }
        markers.clear()
    }

    private fun handleMapMode() {
        setMapGroupVisibility(true)
        setARGroupVisibility(false)
    }

    private fun handleArMode() {
        setMapGroupVisibility(false)
        setARGroupVisibility(true)
    }

    private fun styleMap(googleMap: GoogleMap) {
        googleMap.apply {
            isMyLocationEnabled = true
            uiSettings?.isMyLocationButtonEnabled = false
            setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this@LocationActivity,
                    R.raw.google_maps_style
                )
            )
        }
    }

    private fun setARGroupVisibility(show: Boolean) {
        backToMapButton.isVisible = show
        animateView(show, arLocalizer)
    }

    private fun setMapGroupVisibility(show: Boolean) {
        arViewIcon.isVisible = show
        findAtmButton.isVisible = show

        animateView(show, mapView)
    }

    private fun animateView(show: Boolean, view: View) {
        if (changeModeTransitionAnimation) {
            if (show) {
                view.alpha = 0f
                view.isVisible = true
            }
            view.animate()
                .setDuration(VIEW_MODE_TRANSITION_ANIMATION_DURATION)
                .alpha(if (show) 1.0f else 0.0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        view.isVisible = show
                    }
                })
        } else {
            view.isVisible = show
        }
    }

    private fun moveToLocation(location: LatLng) {
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                location,
                MOVE_TO_LOCATION_ZOOM
            )
        )
    }

    private fun addMarker(
        position: LatLng
    ) {
        val markerOptions = MarkerOptions()
            .apply {
                icon(BitmapDescriptorFactory.fromResource(R.drawable.pockee_map_marker))
                position(position)
                visible(false)
            }

        val pinnedMarker = googleMap.addMarker(markerOptions)
        markers.add(pinnedMarker)
        startDropMarkerAnimation(pinnedMarker, googleMap)
    }

    private fun startDropMarkerAnimation(
        pinnedMarker: Marker,
        map: GoogleMap
    ) {
        val target = pinnedMarker.position
        val projection = map.projection
        val targetPoint = projection.toScreenLocation(target)
        val animationDuration =
            (DROP_MARKER_ANIMATION_DURATION_INIT_VALUE + targetPoint.y * DROP_MARKER_ANIMATION_DURATION_FACTOR).toLong()
        val startPoint = projection.toScreenLocation(pinnedMarker.position)
        startPoint.y -= DROP_MARKER_ANIMATION_POSITION_Y_OFFSET
        val startLatLng = projection.fromScreenLocation(startPoint)

        val propertyLatitude = PropertyValuesHolder.ofFloat(
            LATITUDE_ANIMATION_PROPERTY, startLatLng.latitude.toFloat(),
            target.latitude.toFloat()
        )

        val propertyLongitude = PropertyValuesHolder.ofFloat(
            LONGITUDE_ANIMATION_PROPERTY, startLatLng.longitude.toFloat(),
            target.longitude.toFloat()
        )

        prepareDropMarkerAnimation(
            animationDuration,
            propertyLatitude,
            propertyLongitude,
            pinnedMarker
        ).start()
    }

    private fun prepareDropMarkerAnimation(
        animationDuration: Long,
        propertyLatitude: PropertyValuesHolder?,
        propertyLongitude: PropertyValuesHolder?,
        pinnedMarker: Marker
    ): ValueAnimator {
        return ValueAnimator().apply {
            duration = animationDuration
            interpolator = LinearOutSlowInInterpolator()
            startDelay = DROP_MARKER_ANIMATION_DELAY
            setValues(propertyLatitude, propertyLongitude)
            doOnStart { pinnedMarker.isVisible = true }
            addUpdateListener { animation ->
                val latitude =
                    animation.getAnimatedValue(LATITUDE_ANIMATION_PROPERTY) as? Float ?: 0f
                val longitude =
                    animation.getAnimatedValue(LONGITUDE_ANIMATION_PROPERTY) as? Float ?: 0f
                pinnedMarker.position = LatLng(latitude.toDouble(), longitude.toDouble())
            }
        }
    }

    override fun getSensorsContext() = this
    override fun getARViewLifecycleOwner() = this
    override fun getPermissionActivity() = this

    companion object {
        private const val VIEW_MODE_TRANSITION_ANIMATION_DURATION = 500L
        private const val MOVE_TO_LOCATION_ZOOM = 15f
        private const val LATITUDE_ANIMATION_PROPERTY = "latitude_property"
        private const val LONGITUDE_ANIMATION_PROPERTY = "longitude_property"
        private const val DROP_MARKER_ANIMATION_POSITION_Y_OFFSET = 100
        private const val DROP_MARKER_ANIMATION_DURATION_INIT_VALUE = 200
        private const val DROP_MARKER_ANIMATION_DURATION_FACTOR = 0.6
        private const val DROP_MARKER_ANIMATION_DELAY = 500L
        private const val LOCATION_PERMISSION_REQUEST = 456
    }
}
