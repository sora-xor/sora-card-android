package jp.co.soramitsu.oauth.common.navigation.flow.verification.api

import android.os.Bundle
import com.paywings.onboarding.kyc.android.sdk.data.model.KycUserData
import com.paywings.onboarding.kyc.android.sdk.data.model.UserCredentials
import jp.co.soramitsu.oauth.core.engines.router.api.SoraCardDestinations
import kotlinx.coroutines.flow.SharedFlow

interface VerificationFlow {

    val argsFlow: SharedFlow<Pair<SoraCardDestinations, Bundle>>

    fun onStart(destination: VerificationDestination)

    fun onBack()

    fun onExit()

    fun onLaunchKycContract(
        kycUserData: KycUserData,
        userCredentials: UserCredentials,
        kycReferenceNumber: String
    )

    fun onTryAgain()

    fun onLogout()

    fun onOpenSupport()

    fun onGetMoreXor()

    fun onDepositMoreXor()

    fun onSwapMoreCrypto()

    fun onBuyMoreXor()

    fun onPayIssuance()

}