package jp.co.soramitsu.oauth.common.navigation.flow.verification.impl

import android.os.Bundle
import com.paywings.onboarding.kyc.android.sdk.data.model.KycContractData
import com.paywings.onboarding.kyc.android.sdk.data.model.KycCredentials
import com.paywings.onboarding.kyc.android.sdk.data.model.KycSettings
import com.paywings.onboarding.kyc.android.sdk.data.model.KycUserData
import com.paywings.onboarding.kyc.android.sdk.data.model.UserCredentials
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.core.engines.activityresult.api.OutwardsScreen
import jp.co.soramitsu.oauth.core.engines.activityresult.api.SoraCardResult
import jp.co.soramitsu.oauth.common.navigation.flow.verification.api.VerificationDestination
import jp.co.soramitsu.oauth.common.navigation.flow.verification.api.VerificationFlow
import jp.co.soramitsu.oauth.core.engines.activityresult.api.ActivityResult
import jp.co.soramitsu.oauth.core.engines.router.api.ComposeRouter
import jp.co.soramitsu.oauth.core.engines.router.api.SoraCardDestinations
import java.util.UUID
import javax.inject.Inject

class VerificationFlowImpl @Inject constructor(
    private val composeRouter: ComposeRouter,
    private val activityResult: ActivityResult,
    private val inMemoryRepo: InMemoryRepo
): VerificationFlow {

    private val _args = mutableMapOf<String, Bundle>()

    override val args: Map<String, Bundle> = _args

    override fun onStart(destination: VerificationDestination) =
        when (destination) {
            is VerificationDestination.VerificationRejected -> {
                _args[VerificationDestination.VerificationRejected::class.java.name] = Bundle().apply {
                    putString(
                        VerificationDestination.VerificationRejected.ADDITIONAL_INFO_KEY,
                        destination.additionalInfo
                    )
                }
                composeRouter.setNewStartDestination(destination)
            }
            is VerificationDestination.VerificationFailed -> {
                _args[VerificationDestination.VerificationFailed::class.java.name] = Bundle().apply {
                    putString(
                        VerificationDestination.VerificationRejected.ADDITIONAL_INFO_KEY,
                        destination.additionalInfo
                    )
                }
                composeRouter.setNewStartDestination(destination)
            }
            else -> composeRouter.setNewStartDestination(destination)
        }.run { composeRouter.clearBackStack() }

    override fun onBack() {
        composeRouter.popBack()
    }

    override fun onExit() {
        activityResult.setResult(SoraCardResult.Canceled)
    }

    override fun onLaunchKycContract(
        kycUserData: KycUserData,
        userCredentials: UserCredentials,
        kycReferenceNumber: String
    ) {
        activityResult.launchKycContract(
            KycContractData(
                credentials = KycCredentials(
                    endpointUrl = inMemoryRepo.endpointUrl,
                    username = inMemoryRepo.username,
                    password = inMemoryRepo.password
                ),
                settings = KycSettings(
                    appReferenceId = UUID.randomUUID().toString(),
                    referenceNumber = kycReferenceNumber
                ),
                userCredentials = userCredentials,
                userData = kycUserData
            )
        )
    }

    override fun onTryAgain() {
        if (inMemoryRepo.userAvailableXorAmount < 100)
            composeRouter.setNewStartDestination(VerificationDestination.NotEnoughXor) else
                composeRouter.setNewStartDestination(VerificationDestination.GetPrepared)
    }

    override fun onOpenSupport() {
        // TODO open webView
    }

    override fun onGetMoreXor() {
        composeRouter.navigateTo(VerificationDestination.GetMoreXor)
    }

    override fun onDepositMoreXor() {
        activityResult.setResult(SoraCardResult.NavigateTo(OutwardsScreen.DEPOSIT))
    }

    override fun onSwapMoreCrypto() {
        activityResult.setResult(SoraCardResult.NavigateTo(OutwardsScreen.SWAP))
    }

    override fun onBuyMoreXor() {
        activityResult.setResult(SoraCardResult.NavigateTo(OutwardsScreen.BUY))
    }

    override fun onPayIssuance() {
        TODO("Not yet implemented")
    }
}