package jp.co.soramitsu.oauth.common.domain

import jp.co.soramitsu.oauth.BuildConfig
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification

interface KycRepository {

    suspend fun getReferenceNumber(
        accessToken: String,
        phoneNumber: String?,
        email: String?,
        cardTypeId: String = BuildConfig.KYC_CARD_TYPE_ID,
        ibanTypeId: String = BuildConfig.KYC_IBAN_TYPE_ID
    ): Result<String>

    suspend fun getKycLastFinalStatus(accessToken: String): Result<SoraCardCommonVerification?>

    suspend fun hasFreeKycAttempt(accessToken: String): Result<Boolean>
}
