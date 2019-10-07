package com.netguru.arlocalizerview

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LifecycleOwner

interface ARLocalizerDependencyProvider {
    fun getSensorsContext(): Context
    fun getARViewLifecycleOwner(): LifecycleOwner
    fun getPermissionActivity(): Activity
}
