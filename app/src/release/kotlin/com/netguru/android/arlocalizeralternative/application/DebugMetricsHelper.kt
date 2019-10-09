package com.netguru.android.arlocalizeralternative.application

import android.content.Context
import javax.inject.Singleton
import javax.inject.Inject

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
@Suppress("ALL")
@Singleton
class DebugMetricsHelper @Inject constructor() {

    internal fun init(context: Context) = Unit
}
