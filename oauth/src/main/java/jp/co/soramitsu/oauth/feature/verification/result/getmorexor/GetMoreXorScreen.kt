package jp.co.soramitsu.oauth.feature.verification.result.getmorexor

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import jp.co.soramitsu.oauth.base.compose.SelectableDialog
import jp.co.soramitsu.oauth.base.compose.retrieveString
import jp.co.soramitsu.oauth.common.navigation.activityresult.api.SoraCardResult
import jp.co.soramitsu.oauth.common.navigation.activityresult.api.ActivityResult
import jp.co.soramitsu.oauth.common.navigation.flow.api.NavigationFlow

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
            kycRequirementsUnfulfilledFlow = object : NavigationFlow {
                override fun check() {}

                override fun start() {}

                override fun proceed() {}

                override fun back() {}

                override fun exit() {}
            },
            setActivityResult = object : ActivityResult
                override fun setResult(soraCardResult: SoraCardResult) {}
            }
        )
    )
}