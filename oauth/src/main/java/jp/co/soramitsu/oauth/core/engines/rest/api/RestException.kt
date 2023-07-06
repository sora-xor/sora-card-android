package jp.co.soramitsu.oauth.core.engines.rest.api

sealed class RestException(message: String, error: Throwable?) : Throwable(message, error) {

    class WithCode(
        val code: Int,
        message: String,
        error: Throwable?
    ) : RestException(message, error)

    class WhileSerialization(
        message: String,
        error: Throwable?
    ) : RestException(message, error)

    class SimpleException(
        message: String,
        error: Throwable?
    ) : RestException(message, error)
}

fun RestException.parseToError() =
    when(this) {
        is RestException.SimpleException -> message ?: ""
        is RestException.WhileSerialization -> message ?: ""
        is RestException.WithCode -> message ?: ""
    }

