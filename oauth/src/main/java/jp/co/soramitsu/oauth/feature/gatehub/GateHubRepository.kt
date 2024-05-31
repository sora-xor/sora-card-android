package jp.co.soramitsu.oauth.feature.gatehub

import io.ktor.client.call.body
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.clients.SoraCardTokenException
import jp.co.soramitsu.oauth.common.model.AccessTokenResponse
import jp.co.soramitsu.oauth.feature.AccessTokenValidator
import jp.co.soramitsu.oauth.network.NetworkRequest
import jp.co.soramitsu.oauth.network.SoraCardNetworkClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class GateHubRepository(
    private val apiClient: SoraCardNetworkClient,
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
                token,
                NetworkRequest.GATEWAY_GET_IFRAME.url,
                GetIframeRequestBody(type = 2),
            )
        }.mapCatching { response ->
            when (response.status.value) {
                200 -> {
                    val body = response.body<GetIframeResponse>()
                    IframeModel(
                        code = body.sc,
                        desc = body.sd,
                        url = body.url.orEmpty(),
                    )
                }
                401 -> {
                    throw IllegalStateException("Failed - GetIframe|Unauthorised")
                }
                else -> {
                    throw IllegalStateException("Failed - GetIframe|Internal error")
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
                token,
                NetworkRequest.GATEWAY_ONBOARDED.url,
            )
        }.mapCatching { response ->
            when (response.status.value) {
                200 -> {
                    response.body<OnboardedResponse>().onboarded
                }
                401 -> {
                    throw IllegalStateException("Failed - Onboarded|Unauthorised")
                }
                404 -> {
                    throw IllegalStateException("Failed - Onboarded|Not found")
                }
                else -> {
                    throw IllegalStateException("Failed - Onboarded|Internal error")
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
                token,
                NetworkRequest.GATEWAY_ONBOARD.url,
                OnboardRequestBody(ev, or, sf),
            )
        }.mapCatching { response ->
            when (response.status.value) {
                200 -> {
                    response.body<OnboardResponse>().let {
                        it.sc to it.sd
                    }
                }
                401 -> {
                    throw IllegalStateException("Failed - OnboardUser|Unauthorised")
                }
                else -> {
                    throw IllegalStateException("Failed - OnboardUser|Internal error")
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
