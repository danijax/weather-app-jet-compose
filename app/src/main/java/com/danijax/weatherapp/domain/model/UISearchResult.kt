package com.danijax.weatherapp.domain.model

import androidx.annotation.Keep

sealed interface UISearchResult<out T> {

    @Keep
    data class Success<out T>(val data: T) : UISearchResult<T>
    @Keep
    data class Error(val message: String) : UISearchResult<Nothing>
    data object Empty : UISearchResult<Nothing>
    data object NoAction: UISearchResult<Nothing>

}