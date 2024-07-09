package jp.co.soramitsu.oauth.feature.gatehub

import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.clients.SoraCardTokenException
import jp.co.soramitsu.oauth.common.model.AccessTokenResponse
import jp.co.soramitsu.oauth.feature.AccessTokenValidator
import jp.co.soramitsu.oauth.network.NetworkRequest
import jp.co.soramitsu.oauth.network.SoraCardNetworkClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class GateHubRepository(
    private val apiClient: SoraCardNetworkClient.Adapter,
    private val accessTokenValidator: AccessTokenValidator,
    private val inMemoryRepo: InMemoryRepo,
) {

    suspend fun getIframe(): Result<IframeModel> {
        val token = when (val validity = accessTokenValidator.checkAccessTokenValidity()) {
            is AccessTokenResponse.Token -> validity.token
            else -> null
        }
        if (token == null) return Result.failure(SoraCardTokenException("getIframe"))
        return runCatching {
            apiClient.post(
                header = inMemoryRepo.networkHeader,
                bearerToken = token,
                url = inMemoryRepo.url(null, NetworkRequest.GATEWAY_GET_IFRAME),
                body = GetIframeRequestBody(type = 2),
                deserializer = GetIframeResponse.serializer(),
            ).parse { value, statusCode ->
                if (statusCode == 200 && value != null) {
                    return@parse IframeModel(value.sc, value.sd, value.url.orEmpty())
                }

                when (statusCode) {
                    401 -> error("Failed - GetIframe|Unauthorised")
                    else -> error("Failed - GetIframe|Internal error")
                }
            }
        }
    }

    suspend fun onboarded(): Result<Boolean> {
        val token = when (val validity = accessTokenValidator.checkAccessTokenValidity()) {
            is AccessTokenResponse.Token -> validity.token
            else -> null
        }
        if (token == null) return Result.failure(SoraCardTokenException("Onboarded"))
        return runCatching {
            apiClient.get(
                header = inMemoryRepo.networkHeader,
                bearerToken = token,
                url = inMemoryRepo.url(null, NetworkRequest.GATEWAY_ONBOARDED),
                deserializer = OnboardedResponse.serializer(),
            ).parse { value, statusCode ->
                if (statusCode == 200 && value != null) {
                    return@parse value.onboarded
                }

                when (statusCode) {
                    401 -> error("Failed - Onboarded|Unauthorised")
                    404 -> error("Failed - Onboarded|Not found")
                    else -> error("Failed - Onboarded|Internal error")
                }
            }
        }
    }

    suspend fun onboardUser(): Result<Pair<Int, String>> {
        val token = when (val validity = accessTokenValidator.checkAccessTokenValidity()) {
            is AccessTokenResponse.Token -> validity.token
            else -> null
        }
        if (token == null) return Result.failure(SoraCardTokenException("onboardUser"))
        val ev = inMemoryRepo.ghExpectedExchangeVolume ?: return Result.failure(
            IllegalArgumentException("ExpectedVolume failed"),
        )
        val or = inMemoryRepo.ghExchangeReason.takeIf { it.isNotEmpty() } ?: return Result.failure(
            IllegalArgumentException("OpeningReason failed"),
        )
        val sf = inMemoryRepo.ghSourceOfFunds.takeIf { it.isNotEmpty() } ?: return Result.failure(
            IllegalArgumentException("SourceOfFunds failed"),
        )
        return runCatching {
            apiClient.post(
                header = inMemoryRepo.networkHeader,
                bearerToken = token,
                url = inMemoryRepo.url(null, NetworkRequest.GATEWAY_ONBOARD),
                body = OnboardRequestBody(ev, or, sf),
                deserializer = OnboardResponse.serializer(),
            ).parse { value, statusCode ->
                if (statusCode == 200 && value != null) {
                    value.let { it.sc to it.sd }
                }

                when (statusCode) {
                    401 -> error("Failed - OnboardUser|Unauthorised")
                    else -> error("Failed - OnboardUser|Internal error")
                }
            }
        }
    }
}

@Serializable
private data class OnboardRequestBody(
    @SerialName("ExpectedVolume")
    val ev: Int,
    @SerialName("OpeningReason")
    val or: List<Int>,
    @SerialName("SourceOfFunds")
    val sf: List<Int>,
)

/**
 * @param type 1-ramp withdrawal, 2-ramp deposit, 3-exchange
 */
@Serializable
private data class GetIframeRequestBody(
    @SerialName("IframeType")
    val type: Int,
)

class IframeModel(
    val code: Int,
    val desc: String,
    val url: String,
)

@Serializable
private data class GetIframeResponse(
    @SerialName("CallerReferenceID")
    val crid: String,
    @SerialName("ReferenceID")
    val rif: String,
    @SerialName("StatusCode")
    val sc: Int,
    @SerialName("StatusDescription")
    val sd: String,
    @SerialName("Url")
    val url: String?,
)

@Serializable
private data class OnboardResponse(
    @SerialName("CallerReferenceID")
    val crid: String,
    @SerialName("ReferenceID")
    val rid: String,
    @SerialName("StatusCode")
    val sc: Int,
    @SerialName("StatusDescription")
    val sd: String,
)

@Serializable
private data class OnboardedResponse(
    @SerialName("onboarded")
    val onboarded: Boolean,
)
