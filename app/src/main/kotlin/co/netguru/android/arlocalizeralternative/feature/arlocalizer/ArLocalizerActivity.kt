package co.netguru.android.arlocalizeralternative.feature.arlocalizer

import android.os.Bundle
import android.view.WindowManager
import co.netguru.android.arlocalizeralternative.R
import co.netguru.android.arlocalizeralternative.common.base.BaseActivity
import co.netguru.arlocalizer.ARLocalizerDependencyProvider
import co.netguru.arlocalizer.location.LocationData
import kotlinx.android.synthetic.main.activity_arlocalizer.*
import javax.inject.Inject


class ArLocalizerActivity : BaseActivity(), ARLocalizerDependencyProvider {

    @Inject
    lateinit var locationValidator: LocationValidator

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_arlocalizer)

        arLocalizer.onCreate(this)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        netguruOfficesButton.setOnClickListener {
            arLocalizer.setDestinations(getNetguruOffices())
        }
        gdanskPointsButton.setOnClickListener {
            arLocalizer.setDestinations(getPointsAroundGdanskOffice())
        }
    }

    override fun getSensorsContext() = this

    override fun getARViewLifecycleOwner() = this

    override fun getPermissionActivity() = this

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        arLocalizer.onRequestPermissionResult(requestCode, permissions, grantResults)
    }

    //TODO delete when other source of data will be available
    @Suppress("MagicNumber")
    private fun getNetguruOffices(): List<LocationData>{
        return listOf(LocationData(52.401577, 16.894083), //Poznan
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
        return listOf(LocationData(54.402406, 18.566460),
            LocationData(54.401329, 18.570768),
            LocationData(54.403628, 18.573376),
            LocationData(54.405395, 18.566331),
            LocationData(54.400593, 18.571689)
        )
    }
}
