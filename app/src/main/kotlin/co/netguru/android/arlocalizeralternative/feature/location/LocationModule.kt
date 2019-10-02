package co.netguru.android.arlocalizeralternative.feature.location

import androidx.lifecycle.ViewModelProvider
import co.netguru.android.arlocalizeralternative.feature.DaggerViewModelFactory
import dagger.Binds
import dagger.Module


@Suppress("unused")
@Module(
    includes = [
        LocationActivityModule::class
    ]
)
abstract class LocationModule {

    @Binds
    abstract fun bindViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory
}
