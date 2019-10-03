package co.netguru.android.arlocalizeralternative.feature.location

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
import co.netguru.android.arlocalizeralternative.R
import co.netguru.android.arlocalizeralternative.common.base.BaseActivity
import co.netguru.arlocalizer.ARLocalizerDependencyProvider
import co.netguru.arlocalizer.location.LocationData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_map.*
import javax.inject.Inject


open class LocationActivity : BaseActivity(), ARLocalizerDependencyProvider {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val mapViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get<LocationViewModel>()
    }

    private lateinit var googleMap: GoogleMap
    private val markers: MutableList<Marker> = mutableListOf()
    private var changeModeTransitionAnimation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_map)

        arLocalizer.onCreate(this)
        mapView.addToLifecycle(lifecycle, savedInstanceState)
        setupButtons()
        requestLocationPermission()
    }

    private fun setupButtons() {
        arViewIcon.setOnClickListener {
            changeModeTransitionAnimation = true
            mapViewModel.arModeClick()
        }

        backToMapButton.setOnClickListener {
            changeModeTransitionAnimation = true
            mapViewModel.mapModeClick()
        }

        netguruOfficesButton.setOnClickListener {
            mapViewModel.handleDestinations(getNetguruOffices())
        }

        gdanskPointsButton.setOnClickListener {
            mapViewModel.handleDestinations(getPointsAroundGdanskOffice())
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
            mapViewModel.onMapReady()
            styleMap(googleMap)
        }
    }

    private fun setupObservers() {
        mapViewModel.location.observe(this, Observer { location ->
            moveToLocation(location)
        })
        mapViewModel.destinations.observe(this, Observer { destinations ->
            arLocalizer.setDestinations(destinations)
            showDestinationsOnMap(destinations)
        })
        mapViewModel.viewMode.observe(this, Observer { viewMode ->
            when (viewMode) {
                ViewMode.ARMode -> handleArMode()
                ViewMode.MapMode -> handleMapMode()
            }
        })
    }

    private fun showDestinationsOnMap(destinations: List<LocationData>) {
        if (markers.isNotEmpty()) removeMarkers()
        val latLngDestinations = destinations
            .map { LatLng(it.latitude, it.longitude) }

        fitMapToMarkers(latLngDestinations)
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
        locationButtons.isVisible = show

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

    //TODO delete when other source of data will be available
    @Suppress("MagicNumber")
    private fun getNetguruOffices(): List<LocationData> {
        return listOf(
            LocationData(52.401577, 16.894083), //Poznan
            LocationData(52.239028, 20.995217), //Warszawa
            LocationData(50.069789, 19.945363), //Krakow
            LocationData(51.109812, 17.036580), //Wroclaw
            LocationData(54.402996, 18.569637), //Gdansk
            LocationData(53.128046, 23.172515), //Bialystok
            LocationData(50.259024, 19.019853), //Katowice
            LocationData(51.760939, 19.462599) //Lodz
        )
    }

    //TODO delete when other source of data will be available
    @Suppress("MagicNumber")
    private fun getPointsAroundGdanskOffice(): List<LocationData> {
        return listOf(
            LocationData(54.402406, 18.566460),
            LocationData(54.401329, 18.570768),
            LocationData(54.403628, 18.573376),
            LocationData(54.405395, 18.566331),
            LocationData(54.400593, 18.571689)
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
        startDropMarkerAnimation(pinnedMarker, googleMap)
        markers.add(pinnedMarker)
    }

    private fun fitMapToMarkers(destinations: List<LatLng>) {
        val boundBuilder = LatLngBounds.Builder()

        destinations.forEach { position ->
            boundBuilder.include(position)
        }

        val bounds = boundBuilder.build()
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, CAMERA_BOUNDS_PADDING)

        googleMap.moveCamera(cameraUpdate)
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
        private const val CAMERA_BOUNDS_PADDING = 150
        private const val LATITUDE_ANIMATION_PROPERTY = "latitude_property"
        private const val LONGITUDE_ANIMATION_PROPERTY = "longitude_property"
        private const val DROP_MARKER_ANIMATION_POSITION_Y_OFFSET = 100
        private const val DROP_MARKER_ANIMATION_DURATION_INIT_VALUE = 200
        private const val DROP_MARKER_ANIMATION_DURATION_FACTOR = 0.6
        private const val DROP_MARKER_ANIMATION_DELAY = 500L
        private const val LOCATION_PERMISSION_REQUEST = 456
    }
}
