package jp.co.soramitsu.oauth.feature.session.domain

interface UserSessionRepository {

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
