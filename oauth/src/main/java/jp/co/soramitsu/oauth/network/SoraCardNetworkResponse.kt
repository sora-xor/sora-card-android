package jp.co.soramitsu.oauth.network

/**
 * [SoraCardNetworkResponse] is a data holder class and should contain
 * either non null [value] and status code 200, or null [value] and status code
 *
 * If status code is not available due to serialization, or other kind of throwable issue,
 * the issue must be thrown without this class to be generated
 */
class SoraCardNetworkResponse<T>(
    private val value: T?,
    private val statusCode: Int,
) {
    fun <S> parse(block: (value: T?, statusCode: Int) -> S): S = block(value, statusCode)
}
