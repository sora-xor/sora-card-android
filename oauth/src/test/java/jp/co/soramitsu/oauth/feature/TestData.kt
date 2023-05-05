package jp.co.soramitsu.oauth.feature

import jp.co.soramitsu.oauth.base.sdk.SoraCardInfo
import jp.co.soramitsu.oauth.common.model.IbanStatus
import jp.co.soramitsu.oauth.common.model.KycResponse
import jp.co.soramitsu.oauth.common.model.KycStatus
import jp.co.soramitsu.oauth.common.model.VerificationStatus

object TestData {

    val ACCESS_TOKEN = "access_token"
    val ACCESS_TOKEN_EXPIRATION_TIME = Long.MAX_VALUE

    val REFRESH_TOKEN = "REFRESH_TOKEN"

    val KYC_RESPONSE = KycResponse(
        kycID = "kycID",
        personID = "personID",
        userReferenceNumber = "userReferenceNumber",
        referenceID = "referenceID",
        kycStatus = KycStatus.Successful,
        verificationStatus = VerificationStatus.Accepted,
        ibanStatus = IbanStatus.None,
        updateTime = 1143
    )

    val SORA_CARD_INFO = SoraCardInfo(
        accessToken = ACCESS_TOKEN,
        refreshToken = REFRESH_TOKEN,
        accessTokenExpirationTime = ACCESS_TOKEN_EXPIRATION_TIME
    )
}