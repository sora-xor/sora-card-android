package jp.co.soramitsu.oauth.feature.getmorexor

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import jp.co.soramitsu.androidfoundation.format.retrieveString
import jp.co.soramitsu.oauth.base.compose.SelectableDialog

@Composable
fun ChooseXorPurchaseMethodDialog(getMoreXorViewModel: GetMoreXorViewModel = hiltViewModel()) {
    val state = getMoreXorViewModel.choosePurchaseXorMethodState
    SelectableDialog(
        dialogTitle = state.titleText.retrieveString(),
        dialogDescription = state.descriptionText.retrieveString(),
        selectableChoices = state.methodsTextList.map { it.retrieveString() },
        cancelText = state.cancelText.retrieveString(),
        onChoiceSelectedClickListener = { getMoreXorViewModel.onPurchaseMethodClicked(it) },
        onCancelClickListener = getMoreXorViewModel::onCancelDialogClicked,
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewChooseXorPurchaseMethodDialog() {
    SelectableDialog(
        dialogTitle = "Title",
        dialogDescription = "Desc",
        selectableChoices = listOf("select 1", "select 2"),
        cancelText = "cancel",
        onCancelClickListener = {},
        onChoiceSelectedClickListener = {},
    )
}
