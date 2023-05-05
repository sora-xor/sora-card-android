package jp.co.soramitsu.oauth.network

open class SoraCardNetworkException(message: String, error: Throwable?) : Throwable(message, error)

class CodeNetworkException(val code: Int, message: String, error: Throwable?) :
    SoraCardNetworkException(message, error)

class SerializationNetworkException(message: String, error: Throwable?) :
    SoraCardNetworkException(message, error)

class GeneralNetworkException(message: String, error: Throwable?) :
    SoraCardNetworkException(message, error)
