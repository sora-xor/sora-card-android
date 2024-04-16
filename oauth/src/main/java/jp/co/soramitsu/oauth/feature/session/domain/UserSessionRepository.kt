package jp.co.soramitsu.oauth.feature.session.domain

import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification

interface UserSessionRepository {

    suspend fun setTermsRead()
    suspend fun isTermsRead(): Boolean

    suspend fun getAccessToken(): String

    suspend fun getAccessTokenExpirationTime(): Long

    suspend fun setNewAccessToken(accessToken: String, expirationTime: Long)

    suspend fun setPhoneNumber(phone: String)
    suspend fun getPhoneNumber(): String

    suspend fun logOutUser()

    suspend fun setKycStatus(status: SoraCardCommonVerification)
    suspend fun getKycStatus(): SoraCardCommonVerification?
}
