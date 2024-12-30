package wemabank.com.afb.prod.data

import androidx.annotation.Keep
import wemabank.com.afb.prod.model.ErrorResponse

@Keep
sealed interface Result<out R> {
    @Keep
    data class Success<out T>(val data: T) : Result<T>

    @Keep
    data class Error(val errorResponse: ErrorResponse?, var exception: java.lang.Exception? = null,
                     val errors: List<String>? = null, val errorMessage: String? = null,
                     val responseCode: Int = 0) : Result<Nothing>

}
