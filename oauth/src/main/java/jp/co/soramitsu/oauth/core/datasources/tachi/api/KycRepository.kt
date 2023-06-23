package jp.co.soramitsu.oauth.core.datasources.tachi.api

interface KycRepository {

    suspend fun getReferenceNumber(
        accessToken: String,
        phoneNumber: String?,
        email: String?
    ): Result<String>

    suspend fun getKycStatus(accessToken: String): Result<KycStatus?>

    suspend fun getFreeKycAttemptsInfo(accessToken: String): Result<KycCount>

    suspend fun getCurrentXorEuroPrice(accessToken: String): Result<XorEuroPrice>
}
