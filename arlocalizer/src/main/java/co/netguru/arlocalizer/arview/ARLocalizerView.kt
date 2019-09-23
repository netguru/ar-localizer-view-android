package co.netguru.arlocalizer.arview

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.camera.core.CameraX
import androidx.camera.core.PreviewConfig
import androidx.lifecycle.*
import co.netguru.arlocalizer.*
import co.netguru.arlocalizer.ARLocalizerComponent
import co.netguru.arlocalizer.R
import co.netguru.arlocalizer.compass.CompassData
import co.netguru.arlocalizer.location.LocationData
import co.netguru.arlocalizer.common.ViewState
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.ar_localizer_layout.view.*


class ARLocalizerView : FrameLayout, LifecycleObserver {

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private lateinit var viewModel: IARLocalizerViewModel
    private var arLocalizerComponent: ARLocalizerComponent? = null

    companion object {
        private const val SAVED_STATE = "saved_state"
    }

    private fun init(context: Context) {
        View.inflate(context, R.layout.ar_localizer_layout, this)
    }

    fun onCreate(ARLocalizerDependencyProvider: ARLocalizerDependencyProvider) {
        arLocalizerComponent =
            DaggerARLocalizerComponent.factory().create(ARLocalizerDependencyProvider)
        viewModel = arLocalizerComponent!!.arLocalizerViewModel()
        ARLocalizerDependencyProvider.getARViewLifecycleOwner().lifecycle.addObserver(this)
        checkPermissions()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onActivityStart() {
        startCompass()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onActivityStop() {
        stopCompass()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onActivityDestroy() {
        arLocalizerComponent = null
    }

    private fun stopCompass() {
        ar_label_view.setLowPassFilterAlphaListener(null)
        viewModel.stopCompass()
    }

    fun setDestination(destination: LocationData) {
        viewModel.setDestination(destination)
    }

    private fun startCompass() {
        val lifecycleOwner = context as LifecycleOwner
        ar_label_view.setLowPassFilterAlphaListener {
            viewModel.setLowPassFilterAlpha(it)
        }
        viewModel.compassState.observe(lifecycleOwner, Observer { viewState ->
            when (viewState) {
                is ViewState.Success<CompassData> -> handleSuccessData(viewState.data)
                is ViewState.Error -> showErrorDialog(viewState.message)
            }
        })
        viewModel.startCompass()
    }

    private fun checkPermissions() {
        viewModel.permissionState.observe(context as LifecycleOwner, Observer { permissionState ->
            when (permissionState) {
                PermissionResult.GRANTED -> {
                    texture_view.post { startCameraPreview() }
                }
                PermissionResult.SHOW_RATIONALE -> showRationaleSnackbar()
                PermissionResult.NOT_GRANTED -> { }
            }
        })
        viewModel.checkPermissions()
    }

    private fun handleSuccessData(compassData: CompassData) {
        ar_label_view.setCompassData(compassData)
    }

    private fun startCameraPreview() {
        // Create configuration object for the viewfinder use case
        val previewConfig = PreviewConfig.Builder().apply {
        }.build()

        // Build the viewfinder use case
        val preview = AutoFitPreviewBuilder.build(
            previewConfig,
            texture_view
        )

        CameraX.bindToLifecycle(context as LifecycleOwner, preview)
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(context)
            .setTitle(R.string.error_title)
            .setMessage(resources.getString(R.string.error_message, message))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                startCompass()
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->

            }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    fun onRequestPermissionResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        viewModel.onRequestPermissionResult(requestCode, permissions, grantResults)
    }

    private fun showRationaleSnackbar() {
        Snackbar.make(
            this,
            R.string.essential_permissions_not_granted_info,
            Snackbar.LENGTH_SHORT
        )
            .setAction(R.string.permission_recheck_question) { viewModel.checkPermissions() }
            .setDuration(BaseTransientBottomBar.LENGTH_LONG)
            .show()
    }

    override fun onSaveInstanceState(): Parcelable? {
        return Bundle().apply {
            putParcelable(SAVED_STATE, super.onSaveInstanceState())
            viewModel.onSaveInstanceState(this)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var newState = state
        if (newState is Bundle) {
            viewModel.onRestoreInstanceState(newState)
            newState = newState.getParcelable(SAVED_STATE)
        }
        super.onRestoreInstanceState(newState)
    }
}
