package com.danijax.weatherapp.domain.model

import androidx.annotation.Keep

interface UIResult<out R> {

    @Keep
    data class Success<out T>(val data: T) : UIResult<T>

    @Keep
    data class Error(val message: String) : UIResult<Nothing>


}