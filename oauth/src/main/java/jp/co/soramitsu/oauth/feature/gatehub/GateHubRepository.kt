package jp.co.soramitsu.oauth.feature.gatehub

import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.clients.SoraCardTokenException
import jp.co.soramitsu.oauth.common.model.AccessTokenResponse
import jp.co.soramitsu.oauth.feature.AccessTokenValidator
import jp.co.soramitsu.oauth.network.NetworkRequest
import jp.co.soramitsu.oauth.network.SoraCardNetworkClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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
                header = inMemoryRepo.networkHeader,
                bearerToken = token,
                url = inMemoryRepo.url(null, NetworkRequest.GATEWAY_GET_IFRAME),
                body = Json.encodeToString(GetIframeRequestBody(type = 2)),
                deserializer = GetIframeResponse.serializer(),
            ).parse { value, statusCode, _ ->
                when (statusCode) {
                    200 -> {
                        if (value != null) {
                            if (value.url.isNullOrEmpty()) {
                                error("Failed - GetIframe|Url is not valid")
                            }
                            IframeModel(
                                value.sc,
                                value.sd,
                                value.url,
                            )
                        } else {
                            error("Failed - GetIframe|Null value")
                        }
                    }

                    401 -> error("Failed - GetIframe|Unauthorised")
                    else -> error("Failed - GetIframe|Internal error")
                }
            }
        }
    }

    suspend fun onboarded(): Result<OnboardedResult> {
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
            ).parse { value, statusCode, _ ->
                when (statusCode) {
                    200 -> {
                        if (value != null) {
                            when (value.vs) {
                                0 -> OnboardedResult.Pending
                                1 -> OnboardedResult.Accepted
                                2 -> OnboardedResult.Rejected(value.vm.orEmpty())
                                else -> error("Failed - Onboarded|Unknown status ${value.vs}")
                            }
                        } else {
                            error("Failed - Onboarded|Null value")
                        }
                    }

                    401 -> error("Failed - Onboarded|Unauthorised ($statusCode)")
                    404 -> OnboardedResult.OnboardingNotFound
                    else -> error("Failed - Onboarded|Internal error ($statusCode)")
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
        val es = inMemoryRepo.ghEmploymentStatus ?: return Result.failure(
            IllegalArgumentException("EmploymentStatus failed"),
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
                body = Json.encodeToString(
                    OnboardRequestBody(
                        employmentStatus = es,
                        expectedVolume = ev,
                        openingReason = or,
                        sourceOfFunds = sf,
                        crossBorderDestinationCountries = inMemoryRepo.ghCountriesTo.takeIf { it.isNotEmpty() },
                        crossBorderOriginCountries = inMemoryRepo.ghCountriesFrom.takeIf { it.isNotEmpty() },
                    ),
                ),
                deserializer = OnboardResponse.serializer(),
            ).parse { value, statusCode, message ->
                when (statusCode) {
                    200 -> {
                        if (value != null) {
                            value.sc to value.sd
                        } else {
                            error(
                                "Failed - OnboardUser|Null value|$message",
                            )
                        }
                    }

                    401 -> error("Failed - OnboardUser|Unauthorised")
                    else -> error("Failed - OnboardUser|Internal error ($statusCode $message)")
                }
            }
        }
    }
}

@Serializable
internal data class OnboardRequestBody(
    @SerialName("EmploymentStatus")
    val employmentStatus: Int,
    @SerialName("ExpectedVolume")
    val expectedVolume: Int,
    @SerialName("OpeningReason")
    val openingReason: List<Int>,
    @SerialName("SourceOfFunds")
    val sourceOfFunds: List<Int>,
    @SerialName("CrossBorderDestinationCountries")
    val crossBorderDestinationCountries: List<String>?,
    @SerialName("CrossBorderOriginCountries")
    val crossBorderOriginCountries: List<String>?,
)

/**
 * @param type 1-ramp withdrawal, 2-ramp deposit, 3-exchange
 */
@Serializable
internal data class GetIframeRequestBody(
    @SerialName("IframeType")
    val type: Int,
)

class IframeModel(
    val code: Int,
    val desc: String,
    val url: String,
)

/**
 * @param sc 0 - OK, -1 - invalid parameters, -8 - person is not onboarded
 */
@Serializable
internal data class GetIframeResponse(
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

/**
 * @param sc 0 - ok,
 */
@Serializable
internal data class OnboardResponse(
    @SerialName("CallerReferenceID")
    val crid: String,
    @SerialName("ReferenceID")
    val rid: String,
    @SerialName("StatusCode")
    val sc: Int,
    @SerialName("StatusDescription")
    val sd: String,
)

/**
 * @param vs 0 - pending, 1 - accepted (final status), 2 - rejected (final status)
 * @param vd description of current status
 * @param vm reason for rejected verification
 */
@Serializable
internal data class OnboardedResponse(
    @SerialName("person_id")
    val pid: String,
    @SerialName("verification_status")
    val vs: Int,
    @SerialName("verification_message")
    val vm: String? = null,
    @SerialName("verification_description")
    val vd: String? = null,
    @SerialName("update_time")
    val ut: Int,
)

sealed interface OnboardedResult {
    data object Pending : OnboardedResult
    data class Rejected(val reason: String) : OnboardedResult
    data object Accepted : OnboardedResult
    data object OnboardingNotFound : OnboardedResult
}
