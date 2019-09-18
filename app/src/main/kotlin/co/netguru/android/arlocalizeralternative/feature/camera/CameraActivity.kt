package co.netguru.android.arlocalizeralternative.feature.camera

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import androidx.camera.core.CameraX
import androidx.camera.core.PreviewConfig
import androidx.lifecycle.*
import co.netguru.android.arlocalizeralternative.R
import co.netguru.android.arlocalizeralternative.common.PermissionManager
import co.netguru.android.arlocalizeralternative.common.PermissionResult
import co.netguru.android.arlocalizeralternative.common.ViewState
import co.netguru.android.arlocalizeralternative.common.base.BaseActivity
import co.netguru.android.arlocalizeralternative.feature.compass.CompassData
import co.netguru.android.arlocalizeralternative.feature.compass.CompassViewModel
import co.netguru.android.arlocalizeralternative.feature.compass.LocationValidator
import co.netguru.android.arlocalizeralternative.feature.location.CoordinateType
import co.netguru.android.arlocalizeralternative.feature.location.LocationData
import co.netguru.android.arlocalizeralternative.feature.location.ValidationResult
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_camera.*
import javax.inject.Inject


class CameraActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var locationValidator: LocationValidator

    lateinit var permissionManager: PermissionManager

    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get<CompassViewModel>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_camera)
        permissionManager = PermissionManager(this@CameraActivity)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        set_destination_button.setOnClickListener {
            onDestinationButtonClick()
        }

        if (permissionManager.permissionsGranted()) texture_view.post { startCamera() }
    }

    private fun onDestinationButtonClick() {
        if (permissionManager.permissionsGranted()) showDestinationDialog()
        else permissionManager.requestPermissions()
    }

    private fun startCamera() {
        // Create configuration object for the viewfinder use case
        val previewConfig = PreviewConfig.Builder().apply {
        }.build()

        // Build the viewfinder use case
        val preview = AutoFitPreviewBuilder.build(previewConfig, texture_view)

        CameraX.bindToLifecycle(this, preview)
    }

    override fun onStart() {
        super.onStart()
        startCompass()
    }

    private fun startCompass() {
        ar_label_view.setLowPassFilterAlphaListener {
            viewModel.setLowPassFilterAlpha(it)
        }
        if (permissionManager.permissionsGranted()) {
            viewModel.viewState.observe(this, Observer { viewState ->
                when (viewState) {
                    is ViewState.Success<CompassData> -> handleSuccessData(viewState.data)
                    is ViewState.Error -> showErrorDialog(viewState.message)
                }
            })
            viewModel.startCompass()
        } else permissionManager.requestPermissions()
    }

    private fun handleSuccessData(compassData: CompassData) {
        ar_label_view.setCompassData(compassData)
        compassData.currentLocation?.let {
            current_longitude_value.text = it.longitude.toString()
            current_latitude_value.text = it.latitude.toString()
        }
        compassData.destinationLocation?.let {
            destination_longitude_value.text = it.longitude.toString()
            destination_latitude_value.text = it.latitude.toString()
        }
        compassData.currentDestinationAzimuth.let {
            result_azimuth_value.text = it.toString()
        }
    }

    private fun showDestinationDialog() {
        val view = View.inflate(this, R.layout.destination_dialog, null)
        val latitudeEditText = view.findViewById<TextInputEditText>(R.id.latitude_edit_text)
        val longitudeEditText = view.findViewById<TextInputEditText>(R.id.longitude_edit_text)
        val latitudeTextInputLayout =
            view.findViewById<TextInputLayout>(R.id.latitude_text_input_layout)
        val longitudeTextInputLayout =
            view.findViewById<TextInputLayout>(R.id.longitude_text_input_layout)
        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok, null)
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        dialog.show()

        /*setting OnClick after dialog was created in order to not close the dialog instantly after user pressed
          the button*/
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            validateDestinationInput(
                latitudeEditText.text.toString().toDoubleOrNull(),
                longitudeEditText.text.toString().toDoubleOrNull(),
                latitudeTextInputLayout,
                longitudeTextInputLayout,
                dialog
            )
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.error_title)
            .setMessage(resources.getString(R.string.error_message, message))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                startCompass()
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                this@CameraActivity.finish()
            }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun validateDestinationInput(
        latitude: Double?, longitude: Double?, latitudeTextInputLayout: TextInputLayout,
        longitudeTextInputLayout: TextInputLayout, dialog: AlertDialog
    ) {
        when (locationValidator.validateValue(latitude, CoordinateType.LATITUDE)) {
            ValidationResult.CORRECT_VALUE -> latitudeTextInputLayout.isErrorEnabled = false
            ValidationResult.WRONG_VALUE -> latitudeTextInputLayout.error =
                resources.getString(R.string.latitude_error)
            ValidationResult.EMPTY_VALUE -> latitudeTextInputLayout.error =
                resources.getString(R.string.empty_value_error)
        }
        when (locationValidator.validateValue(longitude, CoordinateType.LONGITUDE)) {
            ValidationResult.CORRECT_VALUE -> longitudeTextInputLayout.isErrorEnabled = false
            ValidationResult.WRONG_VALUE -> longitudeTextInputLayout.error =
                resources.getString(R.string.longitude_error)
            ValidationResult.EMPTY_VALUE -> longitudeTextInputLayout.error =
                resources.getString(R.string.empty_value_error)
        }
        if (!longitudeTextInputLayout.isErrorEnabled && !latitudeTextInputLayout.isErrorEnabled) {
            viewModel.setDestination(
                LocationData(
                    latitude as Double,
                    longitude as Double
                )
            )
            dialog.dismiss()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (permissionManager.getPermissionRequestResult(requestCode, grantResults)) {
            PermissionResult.GRANTED -> {
                showLocationItems(true)
                startCompass()
                startCamera()
            }
            PermissionResult.SHOW_RATIONALE -> showRationaleSnackbar()
            PermissionResult.NOT_GRANTED -> showLocationItems(false)
        }
    }

    private fun showLocationItems(show: Boolean) {
        location_items.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showRationaleSnackbar() {
        Snackbar.make(
            findViewById(android.R.id.content),
            R.string.essential_permissions_not_granted_info,
            Snackbar.LENGTH_SHORT
        )
            .setAction(R.string.permission_recheck_question) { permissionManager.requestPermissions() }
            .setDuration(BaseTransientBottomBar.LENGTH_LONG)
            .show()
    }

    private fun stopCompass() {
        ar_label_view.setLowPassFilterAlphaListener(null)
        viewModel.stopCompass()
    }

    override fun onStop() {
        super.onStop()
        stopCompass()
    }

}
