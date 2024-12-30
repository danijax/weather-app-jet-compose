package wemabank.com.afb.prod.utilities

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import retrofit2.Response
import timber.log.Timber
import wemabank.com.afb.prod.data.DimecResult
import wemabank.com.afb.prod.data.Result
import wemabank.com.afb.prod.model.DimecErrorResponse
import wemabank.com.afb.prod.model.ErrorResponse
import wemabank.com.afb.prod.model.ResponseMessage
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

private const val KEY_SIZE = 128
private const val ENCRYPTION_BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
private const val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
private const val ENCRYPTION_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
private const val SECRET_KEY_NAME = "AFBS2"


fun <T> apiRequestFlow(call: suspend () -> Response<T>): Flow<Result<T>> = channelFlow {
    //emit(Result.Loading)
   // withTimeoutOrNull(60000L) {
        val response = call()
        //Timber.tag("HTTP").d(response.toString())
        try {
            if (response.isSuccessful) {
                response.body()?.let { data ->
                    send(Result.Success(data))
                }
            } else {
                when(response.code()){
                    500, 501, 502, 503, 504, 509, 511 -> {
                        send(Result.Error(ErrorResponse(response.code().toString(), "", ResponseMessage("try again", "An error has occurred"), "")))

                    }

                    else -> {
                        response.errorBody()?.let { error ->
                            val er = error.string()
                            error.close()

                            val parsedError: ErrorResponse =
                                Gson().fromJson(er, ErrorResponse::class.java)
                            send(Result.Error(parsedError))

                        }
                    }
                }

            }
        } catch (e: Exception) {
            Timber.tag("Error Handler").d(e)
            send(Result.Error(exception = e, errorResponse = null))
     //   }
    }
        ?:
        send(Result.Error(ErrorResponse("", "", ResponseMessage("", ""), "")))
}.flowOn(Dispatchers.IO)

fun <T> dimecApiRequestFlow(call: suspend () -> Response<T>): Flow<wemabank.com.afb.prod.data.DimecResult<T>> = channelFlow {
    //emit(Result.Loading)
    withTimeoutOrNull(60000L) {
        val response = call()
        Timber.tag("HTTP").d(response.toString())
        try {
            if (response.isSuccessful) {
                response.body()?.let { data ->
                    send(wemabank.com.afb.prod.data.DimecResult.Success(data))
                }
            } else {
                when(response.code()) {
                    500, 501, 502, 503, 504, 509, 511 -> {
                        send(
                            DimecResult.Error(
                                DimecErrorResponse(
                                    message = "An error has occurred", isSuccessful = false, errors = listOf("An error has occurred"), responseObject = null
                                )
                            )
                        )

                    }

                    else -> {
                        response.errorBody()?.let { error ->
                            val er = error.string()

                            val parsedErrors = Json.decodeFromString<DimecErrorResponse>(er)

                            Timber.tag("Dimec FLow Error").d(er)
                            send(DimecResult.Error( errorResponse = parsedErrors,) )


                        }
                    }
                }

            }
        } catch (e: Exception) {
            Timber.tag("Dimec flow").d(e)
            send(wemabank.com.afb.prod.data.DimecResult.Error(exception = e, errorResponse = null))
        }
    }
        ?:
        send(DimecResult.Error( DimecErrorResponse( "", isSuccessful = false, emptyList() )))
}.flowOn(Dispatchers.IO)


fun initBiometricPrompt(
    activity: AppCompatActivity,
    listener: BiometricAuthListener
): BiometricPrompt {
    // 1
    val executor = ContextCompat.getMainExecutor(activity)

    // 2
    val callback =
    object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            listener.onBiometricAuthenticationError(errorCode, errString.toString())
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            Timber.tag(this.javaClass.simpleName).w("Authentication failed for an unknown reason")
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            listener.onBiometricAuthenticationSuccess(result)
        }
    }

    return BiometricPrompt(activity as AppCompatActivity, executor, callback)

}

private fun generateSecretKey(keyGenParameterSpec: KeyGenParameterSpec): KeyGenerator {
    val keyGenerator = KeyGenerator.getInstance(
        KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
    keyGenerator.init(keyGenParameterSpec)
    //keyGenerator.generateKey()
    return keyGenerator
}

fun getSecretKey(keyName: String): SecretKey? {
    val keyStore = KeyStore.getInstance("AndroidKeyStore")
    keyStore.load(null)
    return keyStore.getKey(keyName, null)?.let { return it as SecretKey }
}

 fun getCipher(): Cipher {
    return Cipher.getInstance(
        ENCRYPTION_ALGORITHM + "/"
            + ENCRYPTION_BLOCK_MODE+ "/"
            + ENCRYPTION_PADDING)
}

fun hasBiometricCapability(context: Context): Int {
    val biometricManager = BiometricManager.from(context)
    return biometricManager.canAuthenticate(Authenticators.BIOMETRIC_STRONG)
}

fun isBiometricReady(context: Context) =
    hasBiometricCapability(context) == BiometricManager.BIOMETRIC_SUCCESS

interface BiometricAuthListener {
    fun onBiometricAuthenticationError(errorCode: Int, toString: String)
    fun onBiometricAuthenticationSuccess(result: BiometricPrompt.AuthenticationResult)

}

fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "${String.format("%02d", minutes)}:${String.format("%02d", remainingSeconds)}"
}

fun formatTime2(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60

    return if (hours > 0) {
        "%02d:%02d:%02d".format(hours, minutes, remainingSeconds)
    } else {
        "%02d:%02d".format(minutes, remainingSeconds)
    }
}
