package co.netguru.arlocalizer

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LifecycleOwner

interface ARLocalizerDependencyProvider {
    fun getSensorsContext(): Context
    fun getARViewLifecycleOwner(): LifecycleOwner
    fun getPermissionActivity(): Activity
}
