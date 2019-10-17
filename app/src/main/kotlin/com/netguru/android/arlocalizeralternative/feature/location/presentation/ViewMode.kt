package com.netguru.android.arlocalizeralternative.feature.location.presentation

sealed class ViewMode {
    object ARMode: ViewMode()
    object MapMode: ViewMode()
}
