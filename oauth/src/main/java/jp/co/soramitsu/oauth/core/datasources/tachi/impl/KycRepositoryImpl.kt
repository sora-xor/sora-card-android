package jp.co.soramitsu.oauth.core.datasources.tachi.impl

import io.ktor.client.call.body
import jp.co.soramitsu.oauth.core.datasources.tachi.api.KycRepository
import jp.co.soramitsu.oauth.core.datasources.tachi.api.GetReferenceNumberRequest
import jp.co.soramitsu.oauth.core.datasources.tachi.api.GetReferenceNumberResponse
import jp.co.soramitsu.oauth.core.datasources.tachi.api.KycCount
import jp.co.soramitsu.oauth.core.datasources.tachi.api.KycResponse
import jp.co.soramitsu.oauth.core.datasources.tachi.api.KycStatus
import jp.co.soramitsu.oauth.core.datasources.tachi.api.XorEuroPrice
import jp.co.soramitsu.oauth.core.engines.network.NetworkRequest
import jp.co.soramitsu.oauth.core.engines.network.SoraCardNetworkClient
import java.util.UUID
import javax.inject.Inject

class KycRepositoryImpl @Inject constructor(
    private val apiClient: SoraCardNetworkClient,
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

    override suspend fun getKycStatus(accessToken: String): Result<KycStatus?> {
        return getKycInfo(accessToken).map {
            it.firstOrNull()?.kycStatus
        }
    }

    private suspend fun getKycInfo(
        accessToken: String
    ): Result<List<KycResponse>> {
        return runCatching {
            apiClient.get(
                bearerToken = accessToken,
                url = NetworkRequest.GET_KYC_STATUS.url
            ).body<List<KycResponse>>()
                .sortedBy { it.updateTime }
        }
    }

    override suspend fun getFreeKycAttemptsInfo(accessToken: String): Result<KycCount> {
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

//    private suspend fun getKycLastFinalStatus(accessToken: String): Result<SoraCardCommonVerification?> {
//        return getKycInfo(accessToken).map { kycStatuses ->
//            val status =
//                getFinalizedFromList(kycStatuses) ?: kycStatuses.getOrNull(kycStatuses.lastIndex)
//            map(status)
//        }
//    }

//    private fun getFinalizedFromList(kycStatuses: List<KycResponse>): KycResponse? {
//        for (i in kycStatuses.lastIndex downTo 0) {
//            val currentStatus = kycStatuses[i].kycStatus
//            if (currentStatus == KycStatus.Completed || currentStatus == KycStatus.Successful) {
//                return kycStatuses[i]
//            }
//        }
//        return null
//    }

//    private fun map(kycResponse: KycResponse?): SoraCardCommonVerification? {
//        if (kycResponse == null) return null
//        return when {
//            (kycResponse.verificationStatus == VerificationStatus.Pending
//                    && (kycResponse.kycStatus == KycStatus.Successful || kycResponse.kycStatus == KycStatus.Completed)) -> {
//                SoraCardCommonVerification.Pending
//            }
//
//            (kycResponse.verificationStatus == VerificationStatus.Accepted
//                    && (kycResponse.kycStatus == KycStatus.Successful || kycResponse.kycStatus == KycStatus.Completed)) -> {
//                SoraCardCommonVerification.Successful
//            }
//
//            kycResponse.kycStatus == KycStatus.Failed -> {
//                SoraCardCommonVerification.Failed
//            }
//
//            kycResponse.kycStatus == KycStatus.Rejected -> {
//                SoraCardCommonVerification.Rejected
//            }
//            else -> null
//        }
//    }

//    private suspend fun hasFreeKycAttempt(accessToken: String): Result<Boolean> {
//        return runCatching {
//            apiClient.get(accessToken, NetworkRequest.GET_KYC_FREE_ATTEMPT_INFO.url)
//                .body<KycAttemptsDto>()
//                .freeAttemptAvailable
//        }
//    }