package co.netguru.android.arlocalizeralternative.application

import android.content.Context
import android.os.StrictMode
import com.facebook.stetho.Stetho
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class that initializes a set of debugging tools
 * for the debug build type and register crash manager for release type.
 * ## Debug type tools:
 * - AndroidDevMetrics
 * - Stetho
 * - StrictMode
 * - Timber
 *
 * ## Release type tools:
 * - CrashManager
 */
@Singleton
class DebugMetricsHelper @Inject constructor() {

    internal fun init(context: Context) {
        // Stetho
        Stetho.initialize(
            Stetho.newInitializerBuilder(context)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                .build()
        )

        // StrictMode
        val threadPolicy = StrictMode.ThreadPolicy.Builder().detectAll()
            .permitDiskReads()
            .permitDiskWrites()
            .penaltyLog() // Must!
            .build()
        StrictMode.setThreadPolicy(threadPolicy)

        val vmPolicy = StrictMode.VmPolicy.Builder()
            .detectAll()
            .penaltyLog() // Must!
            .build()
        StrictMode.setVmPolicy(vmPolicy)

        //Timber
        Timber.plant(Timber.DebugTree())
    }
}
