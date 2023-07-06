package jp.co.soramitsu.oauth.feature.verification.getmorexor

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.paywings.onboarding.kyc.android.sdk.data.model.KycUserData
import com.paywings.onboarding.kyc.android.sdk.data.model.UserCredentials
import jp.co.soramitsu.oauth.theme.views.SelectableDialog
import jp.co.soramitsu.oauth.theme.views.retrieveString
import jp.co.soramitsu.oauth.common.navigation.flow.verification.api.VerificationDestination
import jp.co.soramitsu.oauth.common.navigation.flow.verification.api.VerificationFlow

@Composable
fun ChooseXorPurchaseMethodDialog(
    getMoreXorViewModel: GetMoreXorViewModel = hiltViewModel()
) {
    val state = getMoreXorViewModel.choosePurchaseXorMethodState
    SelectableDialog(
        dialogTitle = state.titleText.retrieveString(),
        dialogDescription = state.descriptionText.retrieveString(),
        selectableChoices = state.methodsTextList.map { it.retrieveString() },
        cancelText = state.cancelText.retrieveString(),
        onChoiceSelectedClickListener = { getMoreXorViewModel.onPurchaseMethodClicked(it) },
        onCancelClickListener = getMoreXorViewModel::onCancelDialogClicked
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewChooseXorPurchaseMethodDialog() {
    ChooseXorPurchaseMethodDialog(
        getMoreXorViewModel = GetMoreXorViewModel(
            verificationFlow = object : VerificationFlow {
                override fun onStart(destination: VerificationDestination) {
                    TODO("Not yet implemented")
                }

                override fun onBack() {
                    TODO("Not yet implemented")
                }

                override fun onExit() {
                    TODO("Not yet implemented")
                }

                override fun onLaunchKycContract(
                    kycUserData: KycUserData,
                    userCredentials: UserCredentials,
                    kycReferenceNumber: String
                ) {
                    TODO("Not yet implemented")
                }

                override fun onTryAgain() {
                    TODO("Not yet implemented")
                }

                override fun onOpenSupport() {
                    TODO("Not yet implemented")
                }

                override fun onGetMoreXor() {
                    TODO("Not yet implemented")
                }

                override fun onDepositMoreXor() {
                    TODO("Not yet implemented")
                }

                override fun onSwapMoreCrypto() {
                    TODO("Not yet implemented")
                }

                override fun onBuyMoreXor() {
                    TODO("Not yet implemented")
                }

                override fun onPayIssuance() {
                    TODO("Not yet implemented")
                }
            }
        )
    )
}