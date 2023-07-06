package jp.co.soramitsu.oauth.common.interactors.user.api

import com.paywings.onboarding.kyc.android.sdk.data.model.KycUserData
import com.paywings.onboarding.kyc.android.sdk.data.model.UserCredentials
import jp.co.soramitsu.oauth.theme.views.Text

sealed interface UserOperationResult {

    object Idle: UserOperationResult

    data class ContractData(
        val kycUserData: KycUserData,
        val userCredentials: UserCredentials,
        val kycReferenceNumber: String
    ): UserOperationResult

    @JvmInline
    value class Error(
        val text: Text
    ): UserOperationResult

}