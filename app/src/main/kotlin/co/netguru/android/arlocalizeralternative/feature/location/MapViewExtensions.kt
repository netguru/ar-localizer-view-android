package co.netguru.android.arlocalizeralternative.feature.location

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.maps.MapView

@Suppress("UnusedPrivateMember")
fun MapView.addToLifecycle(lifecycle: Lifecycle, savedInstanceState: Bundle?) {
    lifecycle.addObserver(object: LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        private fun onActivityCreate() {
            onCreate(savedInstanceState)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        private fun onActivityStart() {
            onStart()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        private fun onActivityResume() {
            onResume()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        private fun onActivityPause() {
            onPause()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        private fun onActivityStop() {
            onStop()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        private fun onActivityDestroy() {
            onDestroy()
        }
    })
}
