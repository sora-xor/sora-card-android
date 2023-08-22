package jp.co.soramitsu.oauth.common.data

import io.ktor.client.call.body
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.model.GetReferenceNumberRequest
import jp.co.soramitsu.oauth.common.model.GetReferenceNumberResponse
import jp.co.soramitsu.oauth.common.model.KycAttemptsDto
import jp.co.soramitsu.oauth.common.model.KycResponse
import jp.co.soramitsu.oauth.common.model.KycStatus
import jp.co.soramitsu.oauth.common.model.IbanAccountResponseWrapper
import jp.co.soramitsu.oauth.common.model.VerificationStatus
import jp.co.soramitsu.oauth.common.model.XorEuroPrice
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.oauth.network.NetworkRequest
import jp.co.soramitsu.oauth.network.SoraCardNetworkClient
import java.util.UUID

class KycRepositoryImpl(
    private val apiClient: SoraCardNetworkClient,
    private val userSessionRepository: UserSessionRepository,
) : KycRepository {

    override suspend fun getReferenceNumber(
        accessToken: String,
        phoneNumber: String?,
        email: String?
    ): Result<String> {
        return runCatching {
            apiClient.post(
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
        }
    }

    private suspend fun getKycInfo(
        accessToken: String,
        baseUrl: String? = null,
    ): Result<List<KycResponse>> {
        return runCatching {
            apiClient.get(
                bearerToken = accessToken,
                url = NetworkRequest.GET_KYC_STATUS.url,
                baseUrl = baseUrl,
            ).body<List<KycResponse>>()
                .sortedBy { it.updateTime }
        }
    }

    override suspend fun getKycLastFinalStatus(accessToken: String, baseUrl: String?): Result<SoraCardCommonVerification> {
        userSessionRepository.getKycStatus()?.let {
            if (it == SoraCardCommonVerification.Successful) return Result.success(SoraCardCommonVerification.Successful)
        }
        return getKycInfo(accessToken, baseUrl).map { kycStatuses ->
            val status = getFinalizedFromList(kycStatuses) ?: kycStatuses.lastOrNull()
            map(status).also {
                userSessionRepository.setKycStatus(it)
            }
        }
    }

    private fun getFinalizedFromList(kycStatuses: List<KycResponse>): KycResponse? {
        for (i in kycStatuses.lastIndex downTo 0) {
            val currentStatus = kycStatuses[i].kycStatus
            if (currentStatus == KycStatus.Completed || currentStatus == KycStatus.Successful) {
                return kycStatuses[i]
            }
        }
        return null
    }

    private fun map(kycResponse: KycResponse?): SoraCardCommonVerification {
        if (kycResponse == null) return SoraCardCommonVerification.NotFound
        return when {
            (kycResponse.verificationStatus == VerificationStatus.Pending
                    && (kycResponse.kycStatus == KycStatus.Successful || kycResponse.kycStatus == KycStatus.Completed)) -> {
                SoraCardCommonVerification.Pending
            }

            (kycResponse.verificationStatus == VerificationStatus.Accepted
                    && (kycResponse.kycStatus == KycStatus.Successful || kycResponse.kycStatus == KycStatus.Completed)) -> {
                SoraCardCommonVerification.Successful
            }

            kycResponse.kycStatus == KycStatus.Failed -> {
                SoraCardCommonVerification.Failed
            }

            kycResponse.kycStatus == KycStatus.Rejected -> {
                SoraCardCommonVerification.Rejected
            }
            else -> SoraCardCommonVerification.NotFound
        }
    }

    override suspend fun hasFreeKycAttempt(accessToken: String): Result<Boolean> {
        return runCatching {
            apiClient.get(accessToken, NetworkRequest.GET_KYC_FREE_ATTEMPT_INFO.url)
                .body<KycAttemptsDto>()
                .freeAttemptAvailable
        }
    }

    override suspend fun getFreeKycAttemptsInfo(accessToken: String): Result<KycAttemptsDto> {
        return runCatching {
            apiClient.get(
                accessToken,
                NetworkRequest.GET_KYC_FREE_ATTEMPT_INFO.url
            ).body()
        }
    }

    override suspend fun getCurrentXorEuroPrice(accessToken: String): Result<XorEuroPrice> {
        return runCatching {
            apiClient.get(
                accessToken,
                NetworkRequest.GET_CURRENT_XOR_EURO_PRICE.url
            ).body()
        }
    }
}
