package jp.co.soramitsu.oauth.common.domain

import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.common.model.KycResponse
import jp.co.soramitsu.oauth.common.model.KycStatus

interface KycRepository {

    suspend fun getReferenceNumber(
        accessToken: String,
        phoneNumber: String?,
        email: String?
    ): Result<String>

    suspend fun getKycLastFinalStatus(accessToken: String): Result<SoraCardCommonVerification?>

    suspend fun hasFreeKycAttempt(accessToken: String): Result<Boolean>
}
