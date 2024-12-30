package com.danijax.weatherapp.core.util


import com.danijax.weatherapp.data.remote.dto.Error
import com.danijax.weatherapp.data.remote.dto.NetworkError
import com.danijax.weatherapp.data.remote.dto.WeatherResponse
import com.danijax.weatherapp.domain.model.WeatherInfo
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn

import retrofit2.Response
import timber.log.Timber
import java.net.UnknownHostException


fun <T> apiRequestFlow(call: suspend () -> Response<T>): Flow<Result<T>> = channelFlow {
    try{
    val response = call()

        if (response.isSuccessful) {
            response.body()?.let { data ->
                send(Result.Success(data))
            }
        } else {
            when (response.code()) {
                500, 501, 502, 503, 504, 509, 511 -> {
                    send(
                        Result.Error(
                            NetworkError.from(
                                Error(
                                    response.code(),
                                    "Server is Unreachable"
                                )
                            )
                        )
                    )

                }

                else -> {
                    response.errorBody()?.let { error ->
                        val er = error.string()
                        error.close()

                        val parsedError: NetworkError =
                            Gson().fromJson(er, NetworkError::class.java)
                        send(Result.Error(parsedError))

                    }
                }
            }

        }
    } catch (e: Exception) {
        Timber.tag("Error Handler").d(e)
        send(Result.Error(NetworkError.generic(e.message ?: "")))
        //   }
    }
        catch (e: UnknownHostException){
            send(Result.Error(NetworkError.generic("No Internet connection")))
        }
        ?: send(Result.Error(NetworkError.generic("Unknown Error has occurred")))
}.flowOn(Dispatchers.IO).catch {
    Timber.tag("Error Handler").d("Caught")

}

fun WeatherResponse.from() = WeatherInfo(
    cityName = location.name,
    temperature = current.temp_c,
    condition = current.condition.text,
    humidity = current.humidity,
    uv = current.uv,
    iconUrl = current.condition.icon

)

