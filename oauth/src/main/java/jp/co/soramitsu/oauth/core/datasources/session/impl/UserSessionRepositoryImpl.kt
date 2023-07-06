package jp.co.soramitsu.oauth.core.datasources.session.impl

import androidx.datastore.preferences.core.stringPreferencesKey
import jp.co.soramitsu.oauth.core.datasources.tachi.api.models.KycStatus
import javax.inject.Inject
import jp.co.soramitsu.oauth.core.datasources.session.api.UserSessionRepository
import jp.co.soramitsu.oauth.core.engines.preferences.api.KeyValuePreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserSessionRepositoryImpl @Inject constructor(
    private val preferences: KeyValuePreferences
) : UserSessionRepository {

    private companion object {
        const val ACCESS_TOKEN_KEY = "ACCESS_TOKEN_KEY"
        const val ACCESS_TOKEN_EXPIRATION_TIME_KEY = "ACCESS_TOKEN_EXPIRATION_TIME_KEY"
        const val REFRESH_TOKEN_KEY = "REFRESH_TOKEN_KEY"
        const val KYC_STATUS_KEY = "KYC_STATUS_KEY"
        const val ADDITIONAL_VERIFICATION_INFO_KEY = "ADDITIONAL_VERIFICATION_INFO_KEY"
    }
    override val kycStatusFlow: Flow<KycStatus> =
        preferences.dataFlow.map {
            it[stringPreferencesKey(KYC_STATUS_KEY)]?.let { status ->
                KycStatus.valueOf(status)
            } ?: KycStatus.NotInitialized
        }

    override val additionalVerificationInfoFlow: Flow<String?> =
        preferences.dataFlow.map {
            it[stringPreferencesKey(ADDITIONAL_VERIFICATION_INFO_KEY)]
        }

    override suspend fun setNewAccessToken(accessToken: String, expirationTime: Long) =
        preferences.run {
            putString(ACCESS_TOKEN_KEY, accessToken)
            putLong(ACCESS_TOKEN_EXPIRATION_TIME_KEY, expirationTime)
        }

    override suspend fun setRefreshToken(refreshToken: String) =
        preferences.putString(REFRESH_TOKEN_KEY, refreshToken)

    override suspend fun setKycStatus(status: KycStatus) =
        preferences.putString(KYC_STATUS_KEY, status.name)

    override suspend fun getAccessToken(): String =
        preferences.getString(ACCESS_TOKEN_KEY)

    override suspend fun getAccessTokenExpirationTime(): Long =
        preferences.getLong(ACCESS_TOKEN_EXPIRATION_TIME_KEY, -1)

    override suspend fun getRefreshToken(): String =
        preferences.getString(REFRESH_TOKEN_KEY)
}
