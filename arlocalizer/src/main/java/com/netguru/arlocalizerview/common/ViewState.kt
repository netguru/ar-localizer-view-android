package com.netguru.arlocalizerview.common

internal sealed class ViewState<T> {
    class Success<T>(val data: T) : ViewState<T>()
    class Error<T>(val message: String) : ViewState<T>()
}
