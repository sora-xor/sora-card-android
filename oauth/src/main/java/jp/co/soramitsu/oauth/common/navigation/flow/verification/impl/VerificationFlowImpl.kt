package jp.co.soramitsu.oauth.common.navigation.flow.verification.impl

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

    override fun onStart(destination: VerificationDestination) =
        when (destination) {
            is VerificationDestination.Start ->
                if (inMemoryRepo.userAvailableXorAmount < 100)
                    composeRouter.setNewStartDestination(SoraCardDestinations.NotEnoughXor) else
                        composeRouter.setNewStartDestination(SoraCardDestinations.GetPrepared)
            is VerificationDestination.VerificationSuccessful ->
                composeRouter.setNewStartDestination(SoraCardDestinations.VerificationSuccessful)
            is VerificationDestination.VerificationInProgress ->
                composeRouter.setNewStartDestination(SoraCardDestinations.VerificationInProgress)
            is VerificationDestination.VerificationRejected ->
                composeRouter.setNewStartDestination(
                    SoraCardDestinations.VerificationRejected(
                        additionalInfo = destination.additionalInfo
                    )
                )
            is VerificationDestination.VerificationFailed ->
                composeRouter.setNewStartDestination(
                    SoraCardDestinations.VerificationRejected(
                        additionalInfo = destination.additionalInfo
                    )
                )
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
            composeRouter.setNewStartDestination(SoraCardDestinations.NotEnoughXor) else
                composeRouter.setNewStartDestination(SoraCardDestinations.GetPrepared)
    }

    override fun onOpenSupport() {
        // TODO open webView
    }

    override fun onGetMoreXor() {
        composeRouter.navigateTo(SoraCardDestinations.GetMoreXor)
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