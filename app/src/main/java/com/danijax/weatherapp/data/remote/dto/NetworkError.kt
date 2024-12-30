package com.danijax.weatherapp.data.remote.dto

import androidx.core.app.NotificationCompat.MessagingStyle.Message

data class NetworkError(
    val error: Error
){
    companion object ServerError{
            fun from(error: Error) = NetworkError(error)
        fun generic(message: String) = NetworkError(Error(0, message))

    }
}

