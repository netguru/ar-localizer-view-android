package co.netguru.android.arlocalizeralternative.feature.arlocalizer

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.LifecycleOwner
import co.netguru.android.arlocalizeralternative.R
import co.netguru.android.arlocalizeralternative.common.base.BaseActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_arlocalizer.*
import javax.inject.Inject


class ArLocalizerActivity : BaseActivity() {

    @Inject
    lateinit var locationValidator: LocationValidator

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_arlocalizer)

        ar_localizer.onCreate(object:
            co.netguru.arlocalizer.ARLocalizerDependencyProvider {
            override fun getPermissionActivity(): Activity {
                return this@ArLocalizerActivity
            }

            override fun getSensorsContext(): Context {
                return this@ArLocalizerActivity
            }

            override fun getARViewLifecycleOwner(): LifecycleOwner {
                return this@ArLocalizerActivity
            }
        })

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        set_destination_button.setOnClickListener {
            onDestinationButtonClick()
        }
    }

    private fun onDestinationButtonClick() {
        showDestinationDialog()
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
            ar_localizer.setDestination(
                co.netguru.arlocalizer.location.LocationData(
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
        ar_localizer.onRequestPermissionResult(requestCode, permissions, grantResults)
    }
}
