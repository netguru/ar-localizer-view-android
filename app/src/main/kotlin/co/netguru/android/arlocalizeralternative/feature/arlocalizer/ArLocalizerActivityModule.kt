package co.netguru.android.arlocalizeralternative.feature.arlocalizer

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ArLocalizerActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeActivity(): ArLocalizerActivity
}
