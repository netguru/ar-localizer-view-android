package com.netguru.arlocalizerview

import com.netguru.arlocalizerview.arview.IARLocalizerViewModel
import com.netguru.arlocalizerview.compass.CompassModule
import dagger.BindsInstance
import dagger.Component


@Component(
    modules = [
        CompassModule::class
    ]
)
internal interface ARLocalizerComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance arLocalizerDependencyProvider: ARLocalizerDependencyProvider): ARLocalizerComponent
    }

    fun arLocalizerViewModel(): IARLocalizerViewModel
    fun arLocalizerDependencyProvider(): ARLocalizerDependencyProvider
}
