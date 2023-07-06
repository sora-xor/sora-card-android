package jp.co.soramitsu.oauth.core.datasources.tachi.api

import jp.co.soramitsu.oauth.core.datasources.tachi.api.models.KycCount
import jp.co.soramitsu.oauth.core.datasources.tachi.api.models.KycStatus
import jp.co.soramitsu.oauth.core.datasources.tachi.api.models.XorEuroPrice

interface TachiRepository {

    suspend fun getReferenceNumber(
        header: String,
        accessToken: String,
        phoneNumber: String?,
        email: String?
    ): Result<String>

    suspend fun getKycStatus(
        header: String,
        accessToken: String
    ): Result<KycStatus?>

    suspend fun getFreeKycAttemptsInfo(
        header: String,
        accessToken: String
    ): Result<KycCount>

    suspend fun getCurrentXorEuroPrice(
        header: String,
        accessToken: String
    ): Result<XorEuroPrice>
}
