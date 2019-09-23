package co.netguru.arlocalizer

import co.netguru.arlocalizer.arview.IARLocalizerViewModel
import co.netguru.arlocalizer.compass.CompassModule
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
