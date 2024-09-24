package jp.co.soramitsu.oauth.common.domain

import jp.co.soramitsu.oauth.base.sdk.contract.IbanInfo
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.common.model.CountryDial
import jp.co.soramitsu.oauth.common.model.KycAttemptsDto
import jp.co.soramitsu.oauth.common.model.KycResponse

interface KycRepository {

    suspend fun getReferenceNumber(
        accessToken: String,
        phoneNumber: String?,
        email: String?,
    ): Result<Pair<Boolean, String>>

    suspend fun getKycLastFinalStatus(
        accessToken: String,
        baseUrl: String? = null,
    ): Result<SoraCardCommonVerification>

    suspend fun getIbanStatus(accessToken: String, baseUrl: String? = null): Result<IbanInfo?>

    fun getCachedKycResponse(): Pair<SoraCardCommonVerification, KycResponse?>?

    suspend fun hasFreeKycAttempt(accessToken: String): Result<Boolean>

    suspend fun getFreeKycAttemptsInfo(accessToken: String): Result<KycAttemptsDto>

    suspend fun getCurrentXorEuroPrice(accessToken: String): Result<Double>

    suspend fun getRetryFee(): String
    suspend fun getApplicationFee(baseUrl: String? = null): String

    suspend fun getCountries(): List<CountryDial>
}
