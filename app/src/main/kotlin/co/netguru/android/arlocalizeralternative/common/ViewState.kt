package co.netguru.android.arlocalizeralternative.common

sealed class ViewState<T> {
    class Success<T>(val data: T) : ViewState<T>()
    class Error<T>(val message: String) : ViewState<T>()
}