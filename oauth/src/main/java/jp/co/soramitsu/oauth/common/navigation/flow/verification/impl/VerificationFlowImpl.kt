package jp.co.soramitsu.oauth.common.navigation.flow.verification.impl

import com.paywings.onboarding.kyc.android.sdk.data.model.KycContractData
import com.paywings.onboarding.kyc.android.sdk.data.model.KycCredentials
import com.paywings.onboarding.kyc.android.sdk.data.model.KycSettings
import com.paywings.onboarding.kyc.android.sdk.data.model.KycUserData
import com.paywings.onboarding.kyc.android.sdk.data.model.UserCredentials
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.common.navigation.activityresult.api.OutwardsScreen
import jp.co.soramitsu.oauth.common.navigation.activityresult.api.SoraCardResult
import jp.co.soramitsu.oauth.common.navigation.flow.verification.api.VerificationDestination
import jp.co.soramitsu.oauth.common.navigation.flow.verification.api.VerificationFlow
import jp.co.soramitsu.oauth.common.navigation.activityresult.api.ActivityResult
import jp.co.soramitsu.oauth.common.navigation.router.api.ComposeRouter
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
                    composeRouter.setNewStartDestination("NOT_ENOUGH_XOR") else
                    composeRouter.setNewStartDestination("GET_PREPARED")
            is VerificationDestination.VerificationSuccessful ->
                composeRouter.setNewStartDestination("VERIFICATION_IN_SUCCESSFUL")
            is VerificationDestination.VerificationInProgress ->
                composeRouter.setNewStartDestination("VERIFICATION_IN_PROGRESS")
            is VerificationDestination.VerificationRejected ->
                composeRouter.setNewStartDestination("VERIFICATION_REJECTED")
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
        TODO("Not yet implemented")
    }

    override fun onOpenSupport() {
        // TODO open webView
    }

    override fun onGetMoreXor() {
        TODO("Not yet implemented")
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