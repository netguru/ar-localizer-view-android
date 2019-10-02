package co.netguru.android.arlocalizeralternative.application

import android.content.Context
import co.netguru.android.arlocalizeralternative.common.error.ErrorHandlerModule
import co.netguru.android.arlocalizeralternative.feature.location.LocationModule
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
        LocationModule::class
    ]
)
internal interface ApplicationComponent : AndroidInjector<App> {

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance context: Context): ApplicationComponent
    }
}
