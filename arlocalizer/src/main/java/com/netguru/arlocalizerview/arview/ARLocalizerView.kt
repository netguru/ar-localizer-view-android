package com.netguru.arlocalizerview.arview

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.camera.core.CameraX
import androidx.camera.core.PreviewConfig
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import com.netguru.arlocalizerview.ARLocalizerComponent
import com.netguru.arlocalizerview.ARLocalizerDependencyProvider
import com.netguru.arlocalizerview.DaggerARLocalizerComponent
import com.netguru.arlocalizerview.PermissionResult
import com.netguru.arlocalizerview.R
import com.netguru.arlocalizerview.common.ViewState
import com.netguru.arlocalizerview.compass.CompassData
import com.netguru.arlocalizerview.location.LocationData
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.ar_localizer_layout.view.*


@Suppress("UnusedPrivateMember", "TooManyFunctions")
class ARLocalizerView : FrameLayout, LifecycleObserver {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, attributeSetId: Int) : super(
        context,
        attrs,
        attributeSetId
    )

    init {
        View.inflate(context, R.layout.ar_localizer_layout, this)
    }

    private lateinit var viewModel: IARLocalizerViewModel
    private lateinit var arLocalizerComponent: ARLocalizerComponent

    companion object {
        private const val SAVED_STATE = "saved_state"
    }

    fun onCreate(arLocalizerDependencyProvider: ARLocalizerDependencyProvider) {
        arLocalizerComponent =
            DaggerARLocalizerComponent.factory().create(arLocalizerDependencyProvider)
        viewModel = arLocalizerComponent.arLocalizerViewModel()
        arLocalizerComponent.arLocalizerDependencyProvider().getARViewLifecycleOwner()
            .lifecycle.addObserver(this)
        checkPermissions()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onActivityDestroy() {
        ar_label_view.setLowPassFilterAlphaListener(null)
    }

    fun setDestinations(destinations: List<LocationData>) {
        viewModel.setDestinations(destinations)
    }

    private fun observeCompassState() {
        ar_label_view.setLowPassFilterAlphaListener {
            viewModel.setLowPassFilterAlpha(it)
        }
        viewModel.compassState().observe(
            arLocalizerComponent.arLocalizerDependencyProvider().getARViewLifecycleOwner(),
            Observer { viewState ->
                when (viewState) {
                    is ViewState.Success<CompassData> -> handleSuccessData(viewState.data)
                    is ViewState.Error -> showErrorDialog(viewState.message)
                }
            })
    }

    private fun checkPermissions() {
        viewModel.permissionState.observe(
            arLocalizerComponent.arLocalizerDependencyProvider().getARViewLifecycleOwner(),
            Observer { permissionState ->
                when (permissionState) {
                    PermissionResult.GRANTED -> {
                        texture_view.post { startCameraPreview() }
                        observeCompassState()
                    }
                    PermissionResult.SHOW_RATIONALE -> showRationaleSnackbar()
                    PermissionResult.NOT_GRANTED -> Unit
                }
            })
        viewModel.checkPermissions()
    }

    private fun handleSuccessData(compassData: CompassData) {
        ar_label_view.setCompassData(compassData)
    }

    private fun startCameraPreview() {
        val preview = AutoFitPreviewBuilder.build(
            PreviewConfig.Builder().build(),
            texture_view
        )
        CameraX.bindToLifecycle(
            arLocalizerComponent.arLocalizerDependencyProvider().getARViewLifecycleOwner(),
            preview
        )
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(context)
            .setTitle(R.string.error_title)
            .setMessage(resources.getString(R.string.error_message, message))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                observeCompassState()
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
