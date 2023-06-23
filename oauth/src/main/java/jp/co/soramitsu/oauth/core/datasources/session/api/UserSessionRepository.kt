package jp.co.soramitsu.oauth.core.datasources.session.api

import jp.co.soramitsu.oauth.core.datasources.tachi.api.KycStatus
import kotlinx.coroutines.flow.Flow

interface UserSessionRepository {

    suspend fun setKycStatus(
        status: KycStatus
    )

    val kycStatusFlow: Flow<KycStatus>

    suspend fun getRefreshToken(): String

    suspend fun getAccessToken(): String

    suspend fun getAccessTokenExpirationTime(): Long

    suspend fun signInUser(
        refreshToken: String,
        accessToken: String,
        expirationTime: Long
    )

    suspend fun setNewAccessToken(
        accessToken: String,
        expirationTime: Long
    )

    suspend fun setRefreshToken(
        refreshToken: String
    )

    suspend fun setUserId(userId: String?)

    suspend fun setPersonId(personId: String?)

    suspend fun getUserId(): String

    suspend fun getPersonId(): String

    suspend fun logOutUser()
}
