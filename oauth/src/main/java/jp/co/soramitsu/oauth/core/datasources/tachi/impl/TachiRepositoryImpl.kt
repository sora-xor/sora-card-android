package jp.co.soramitsu.oauth.core.datasources.tachi.impl

import io.ktor.client.call.body
import jp.co.soramitsu.oauth.core.datasources.tachi.api.TachiRepository
import jp.co.soramitsu.oauth.core.datasources.tachi.api.models.GetReferenceNumberRequest
import jp.co.soramitsu.oauth.core.datasources.tachi.api.models.GetReferenceNumberResponse
import jp.co.soramitsu.oauth.core.datasources.tachi.api.models.KycCount
import jp.co.soramitsu.oauth.core.datasources.tachi.api.models.KycResponse
import jp.co.soramitsu.oauth.core.datasources.tachi.api.models.KycStatus
import jp.co.soramitsu.oauth.core.datasources.tachi.api.models.XorEuroPrice
import jp.co.soramitsu.oauth.core.engines.rest.api.RestClient
import java.util.UUID
import javax.inject.Inject

class TachiRepositoryImpl @Inject constructor(
    private val restClient: RestClient
) : TachiRepository {

    override suspend fun getReferenceNumber(
        header: String,
        accessToken: String,
        phoneNumber: String?,
        email: String?
    ): Result<String> {
        return runCatching {
            restClient.post(
                header = header,
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

    override suspend fun getKycStatus(
        header: String,
        accessToken: String
    ): Result<KycStatus?> {
        return runCatching {
            restClient.get(
                header = header,
                bearerToken = accessToken,
                url = NetworkRequest.GET_KYC_STATUS.url
            ).body<List<KycResponse>>()
                .sortedBy { it.updateTime }
                .firstOrNull()?.kycStatus
        }
    }

    override suspend fun getFreeKycAttemptsInfo(
        header: String,
        accessToken: String
    ): Result<KycCount> {
        return runCatching {
            restClient.get(
                header = header,
                bearerToken = accessToken,
                url = NetworkRequest.GET_KYC_FREE_ATTEMPT_INFO.url
            ).body()
        }
    }

    override suspend fun getCurrentXorEuroPrice(
        header: String,
        accessToken: String
    ): Result<XorEuroPrice> {
        return runCatching {
            restClient.get(
                header = header,
                bearerToken = accessToken,
                url = NetworkRequest.GET_KYC_FREE_ATTEMPT_INFO.url
            ).body()
        }
    }
}