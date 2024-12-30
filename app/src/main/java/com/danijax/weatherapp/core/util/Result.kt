package com.danijax.weatherapp.core.util

import androidx.annotation.Keep
import com.danijax.weatherapp.data.remote.dto.NetworkError
@Keep
sealed interface Result<out R> {
    @Keep
    data class Success<out T>(val data: T) : Result<T>

    @Keep
    data class Error(val string: NetworkError) : Result<Nothing>

}
