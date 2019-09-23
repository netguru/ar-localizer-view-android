package co.netguru.android.arlocalizeralternative.application

import android.app.Application
import co.netguru.android.arlocalizeralternative.common.error.ErrorHandlerModule
import co.netguru.android.arlocalizeralternative.feature.arlocalizer.ArLocalizerActivityModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AndroidSupportInjectionModule::class,
        ErrorHandlerModule::class,
        ArLocalizerActivityModule::class
    ]
)
internal interface ApplicationComponent : AndroidInjector<App> {

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance application: Application): ApplicationComponent
    }
}
