package jp.co.soramitsu.oauth.feature.session.data

import jp.co.soramitsu.oauth.base.data.SoraCardDataStore
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository

class UserSessionRepositoryImpl(
    private val dataStore: SoraCardDataStore,
) : UserSessionRepository {

    private companion object {
        const val TERMS_READ = "TERMS_READ"
        const val ACCESS_TOKEN_KEY = "ACCESS_TOKEN_KEY"
        const val ACCESS_TOKEN_EXPIRATION_TIME_KEY = "ACCESS_TOKEN_EXPIRATION_TIME_KEY"
        const val KYC = "kyc_status"
        const val TERMS_VALUE = 1L
    }

    override suspend fun isTermsRead(): Boolean = dataStore.getLong(TERMS_READ, 0) == TERMS_VALUE

    override suspend fun setTermsRead() {
        dataStore.putLong(TERMS_READ, TERMS_VALUE)
    }

    override suspend fun setKycStatus(status: SoraCardCommonVerification) {
        dataStore.putString(KYC, status.name)
    }

    override suspend fun getKycStatus(): SoraCardCommonVerification? {
        val data = dataStore.getString(KYC)
        return runCatching { SoraCardCommonVerification.valueOf(data) }.getOrNull()
    }

    override suspend fun getAccessToken(): String = dataStore.getString(ACCESS_TOKEN_KEY)

    override suspend fun getAccessTokenExpirationTime(): Long =
        dataStore.getLong(ACCESS_TOKEN_EXPIRATION_TIME_KEY, 0)

    override suspend fun setNewAccessToken(accessToken: String, expirationTime: Long) {
        dataStore.putString(ACCESS_TOKEN_KEY, accessToken)
        dataStore.putLong(ACCESS_TOKEN_EXPIRATION_TIME_KEY, expirationTime)
    }

    override suspend fun logOutUser() {
        dataStore.clearAll()
    }
}
