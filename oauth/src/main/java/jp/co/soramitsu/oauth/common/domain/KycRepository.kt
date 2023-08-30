package jp.co.soramitsu.oauth.common.domain

import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.common.model.KycAttemptsDto
import jp.co.soramitsu.oauth.common.model.IbanAccountResponseWrapper
import jp.co.soramitsu.oauth.common.model.XorEuroPrice

interface KycRepository {

    suspend fun getReferenceNumber(
        accessToken: String,
        phoneNumber: String?,
        email: String?
    ): Result<String>

    suspend fun getKycLastFinalStatus(accessToken: String, baseUrl: String? = null): Result<SoraCardCommonVerification>

    suspend fun hasFreeKycAttempt(accessToken: String): Result<Boolean>

    suspend fun getFreeKycAttemptsInfo(accessToken: String): Result<KycAttemptsDto>

    suspend fun getCurrentXorEuroPrice(accessToken: String): Result<XorEuroPrice>

    suspend fun getUserIbanAccount(accessToken: String): Result<IbanAccountResponseWrapper>
}
