package jp.co.soramitsu.oauth.core.datasources.session.api

import jp.co.soramitsu.oauth.core.datasources.tachi.api.models.KycStatus
import kotlinx.coroutines.flow.Flow

interface UserSessionRepository {

    val kycStatusFlow: Flow<KycStatus>

    val additionalVerificationInfoFlow: Flow<String?>

    suspend fun setNewAccessToken(
        accessToken: String,
        expirationTime: Long
    )

    suspend fun setRefreshToken(
        refreshToken: String
    )

    suspend fun setKycStatus(
        status: KycStatus
    )

    suspend fun getAccessToken(): String

    suspend fun getAccessTokenExpirationTime(): Long

    suspend fun getRefreshToken(): String
}
