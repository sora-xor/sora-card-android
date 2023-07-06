package jp.co.soramitsu.oauth.common.navigation.flow.verification.api

import com.paywings.onboarding.kyc.android.sdk.data.model.KycUserData
import com.paywings.onboarding.kyc.android.sdk.data.model.UserCredentials

interface VerificationFlow {

    fun onStart(destination: VerificationDestination)

    fun onBack()

    fun onExit()

    fun onLaunchKycContract(
        kycUserData: KycUserData,
        userCredentials: UserCredentials,
        kycReferenceNumber: String
    )

    fun onTryAgain()

    fun onOpenSupport()

    fun onGetMoreXor()

    fun onDepositMoreXor()

    fun onSwapMoreCrypto()

    fun onBuyMoreXor()

    fun onPayIssuance()

}