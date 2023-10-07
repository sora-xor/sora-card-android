package jp.co.soramitsu.oauth.common.data

import io.ktor.client.call.body
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.model.FeesDto
import jp.co.soramitsu.oauth.common.model.GetReferenceNumberRequest
import jp.co.soramitsu.oauth.common.model.GetReferenceNumberResponse
import jp.co.soramitsu.oauth.common.model.KycAttemptsDto
import jp.co.soramitsu.oauth.common.model.KycResponse
import jp.co.soramitsu.oauth.common.model.KycStatus
import jp.co.soramitsu.oauth.common.model.VerificationStatus
import jp.co.soramitsu.oauth.common.model.XorEuroPrice
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.oauth.network.NetworkRequest
import jp.co.soramitsu.oauth.network.SoraCardNetworkClient
import jp.co.soramitsu.xnetworking.basic.common.Utils.toDoubleNan
import java.util.UUID

class KycRepositoryImpl(
    private val apiClient: SoraCardNetworkClient,
    private val userSessionRepository: UserSessionRepository,
) : KycRepository {

    private var cacheReference: String = ""

    override suspend fun getReferenceNumber(
        accessToken: String,
        phoneNumber: String?,
        email: String?
    ): Result<String> {
        if (cacheReference.isNotEmpty()) return Result.success(cacheReference)
        return runCatching {
            val ref = apiClient.post(
                bearerToken = accessToken,
                url = NetworkRequest.GET_REFERENCE_NUMBER.url,
                body = GetReferenceNumberRequest(
                    referenceID = UUID.randomUUID().toString(),
                    mobileNumber = phoneNumber,
                    email = email,
                    addressChanged = false,
                    documentChanged = false,
                    additionalData = ""
                )
            ).body<GetReferenceNumberResponse>()
                .referenceNumber
            cacheReference = ref
            cacheReference
        }
    }

    private suspend fun getKycInfo(
        accessToken: String,
        baseUrl: String? = null,
    ): Result<KycResponse> = runCatching {
        apiClient.get(
            bearerToken = accessToken,
            url = NetworkRequest.GET_KYC_STATUS.url,
            baseUrl = baseUrl,
        ).body<KycResponse>()
    }

    private var cacheKycResponse: Pair<SoraCardCommonVerification, KycResponse>? = null

    override fun getCachedKycResponse(): Pair<SoraCardCommonVerification, KycResponse>? {
        val local = cacheKycResponse
        cacheKycResponse = null
        return local
    }

    override suspend fun getKycLastFinalStatus(
        accessToken: String,
        baseUrl: String?
    ): Result<SoraCardCommonVerification> {
        userSessionRepository.getKycStatus()?.let {
            if (it == SoraCardCommonVerification.Successful) return Result.success(
                SoraCardCommonVerification.Successful
            )
        }
        return getKycInfo(accessToken, baseUrl).map { kycStatus ->
            mapKycStatus(kycStatus).also {
                cacheKycResponse = it to kycStatus
                cacheReference = if (it == SoraCardCommonVerification.Rejected) {
                    ""
                } else {
                    kycStatus.userReferenceNumber
                }
                userSessionRepository.setKycStatus(it)
            }
        }
    }

    private fun mapKycStatus(kycResponse: KycResponse): SoraCardCommonVerification {
        return when {
            (kycResponse.verificationStatus == VerificationStatus.Accepted) -> {
                SoraCardCommonVerification.Successful
            }

            (kycResponse.kycStatus == KycStatus.Successful || kycResponse.kycStatus == KycStatus.Completed) -> {
                SoraCardCommonVerification.Pending
            }

            kycResponse.kycStatus == KycStatus.Failed -> {
                SoraCardCommonVerification.Failed
            }

            kycResponse.kycStatus == KycStatus.Started -> {
                SoraCardCommonVerification.Started
            }

            kycResponse.kycStatus == KycStatus.Retry -> {
                SoraCardCommonVerification.Retry
            }

            kycResponse.kycStatus == KycStatus.Rejected -> {
                SoraCardCommonVerification.Rejected
            }

            else -> SoraCardCommonVerification.NotFound
        }
    }

    override suspend fun hasFreeKycAttempt(accessToken: String): Result<Boolean> =
        getFreeKycAttemptsInfo(accessToken).map { it.freeAttemptAvailable }

    override suspend fun getFreeKycAttemptsInfo(accessToken: String): Result<KycAttemptsDto> {
        return runCatching {
            apiClient.get(
                accessToken,
                NetworkRequest.GET_KYC_FREE_ATTEMPT_INFO.url
            ).body()
        }
    }

    private var feesCache: Pair<String, String>? = null

    override suspend fun getRetryFee(): String =
        feesCache?.first ?: getFeesInternal().getOrNull()?.let {
            feesCache = it
            it.first
        } ?: ""

    override suspend fun getApplicationFee(baseUrl: String?): String =
        feesCache?.second ?: getFeesInternal(baseUrl).getOrNull()?.let {
            feesCache = it
            it.second
        } ?: ""

    private suspend fun getFeesInternal(baseUrl: String? = null): Result<Pair<String, String>> =
        runCatching {
            val dto =
                apiClient.get(bearerToken = null, url = NetworkRequest.FEES.url, baseUrl = baseUrl)
                    .body<FeesDto>()
            dto.retryFee to dto.applicationFee
        }

    override suspend fun getCurrentXorEuroPrice(accessToken: String): Result<Double> {
        return runCatching {
            apiClient.get(
                accessToken,
                NetworkRequest.GET_CURRENT_XOR_EURO_PRICE.url
            ).body<XorEuroPrice>()
        }.mapCatching {
            it.price.toDoubleNan() ?: throw IllegalArgumentException("XOR Euro price failed")
        }
    }
}
