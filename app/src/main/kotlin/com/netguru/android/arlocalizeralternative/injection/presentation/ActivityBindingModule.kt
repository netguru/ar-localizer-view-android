package com.netguru.android.arlocalizeralternative.injection.presentation

import com.netguru.android.arlocalizeralternative.feature.location.presentation.LocationActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector
    abstract fun contributeLocationActivity(): LocationActivity
}
