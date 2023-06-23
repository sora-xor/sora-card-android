package jp.co.soramitsu.oauth.core.datasources.session.impl

import jp.co.soramitsu.oauth.core.datasources.tachi.api.KycStatus
import javax.inject.Inject
import jp.co.soramitsu.oauth.core.engines.SoraCardDataStore
import jp.co.soramitsu.oauth.core.datasources.session.api.UserSessionRepository
import kotlinx.coroutines.flow.Flow

class UserSessionRepositoryImpl @Inject constructor(
    private val dataStore: SoraCardDataStore
) : UserSessionRepository {

    private companion object {
        const val REFRESH_TOKEN_KEY = "REFRESH_TOKEN_KEY"
        const val ACCESS_TOKEN_KEY = "ACCESS_TOKEN_KEY"
        const val ACCESS_TOKEN_EXPIRATION_TIME_KEY = "ACCESS_TOKEN_EXPIRATION_TIME_KEY"
        const val USER_ID = "USER_ID"
        const val PERSON_ID = "PERSON_ID"
    }

    override suspend fun setKycStatus(status: KycStatus) {
        TODO("Not yet implemented")
    }

    override val kycStatusFlow: Flow<KycStatus>
        get() = TODO()

    override suspend fun getRefreshToken(): String =
        dataStore.getString(REFRESH_TOKEN_KEY)

    override suspend fun getAccessToken(): String =
        dataStore.getString(ACCESS_TOKEN_KEY)

    override suspend fun getAccessTokenExpirationTime(): Long =
        dataStore.getLong(ACCESS_TOKEN_EXPIRATION_TIME_KEY, 0)

    override suspend fun signInUser(
        refreshToken: String,
        accessToken: String,
        expirationTime: Long
    ) {
        dataStore.putString(REFRESH_TOKEN_KEY, refreshToken)
        dataStore.putString(ACCESS_TOKEN_KEY, accessToken)
        dataStore.putLong(ACCESS_TOKEN_EXPIRATION_TIME_KEY, expirationTime)
    }

    override suspend fun setNewAccessToken(accessToken: String, expirationTime: Long) {
        dataStore.putString(ACCESS_TOKEN_KEY, accessToken)
        dataStore.putLong(ACCESS_TOKEN_EXPIRATION_TIME_KEY, expirationTime)
    }

    override suspend fun setRefreshToken(refreshToken: String) {
        dataStore.putString(REFRESH_TOKEN_KEY, refreshToken)
    }

    override suspend fun setUserId(userId: String?) {
        dataStore.putString(USER_ID, userId ?: "")
    }

    override suspend fun setPersonId(personId: String?) {
        dataStore.putString(PERSON_ID, personId ?: "")
    }

    override suspend fun getUserId(): String = dataStore.getString(USER_ID)

    override suspend fun getPersonId(): String = dataStore.getString(PERSON_ID)

    override suspend fun logOutUser() {
        dataStore.clearAll()
    }
}
