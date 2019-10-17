package com.netguru.android.arlocalizeralternative.application

import android.content.Context
import com.netguru.android.arlocalizeralternative.common.error.ErrorHandlerModule
import com.netguru.android.arlocalizeralternative.injection.presentation.ActivityBindingModule
import com.netguru.android.arlocalizeralternative.injection.presentation.ViewModelModule
import com.netguru.android.arlocalizeralternative.feature.location.LocationModule
import com.netguru.android.arlocalizeralternative.injection.data.NetworkModule
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
        NetworkModule::class,
        ActivityBindingModule::class,
        ViewModelModule::class,
        LocationModule::class
    ]
)
internal interface ApplicationComponent : AndroidInjector<App> {

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance context: Context): ApplicationComponent
    }
}
