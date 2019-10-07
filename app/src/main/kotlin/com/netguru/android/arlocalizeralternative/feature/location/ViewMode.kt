package com.netguru.android.arlocalizeralternative.feature.location

sealed class ViewMode {
    object ARMode: ViewMode()
    object MapMode: ViewMode()
}
