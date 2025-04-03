package jp.co.soramitsu.oauth.feature.cardissuance

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import jp.co.soramitsu.androidfoundation.format.retrieveString
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.feature.cardissuance.state.CardIssuanceScreenState
import jp.co.soramitsu.oauth.uiscreens.compose.BalanceIndicator
import jp.co.soramitsu.oauth.uiscreens.compose.Screen
import jp.co.soramitsu.oauth.uiscreens.compose.ScreenStatus
import jp.co.soramitsu.oauth.uiscreens.styledui.FilledLargePrimaryButton
import jp.co.soramitsu.oauth.uiscreens.theme.AuthSdkTheme
import jp.co.soramitsu.ui_core.component.button.OutlinedButton
import jp.co.soramitsu.ui_core.component.button.properties.Order
import jp.co.soramitsu.ui_core.component.button.properties.Size
import jp.co.soramitsu.ui_core.component.card.ContentCard
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

@Composable
fun CardIssuanceScreen(viewModel: CardIssuanceViewModel = hiltViewModel()) {
    BackHandler {
        viewModel.onToolbarNavigation()
    }
    Screen(
        viewModel = viewModel,
    ) { scrollState ->
        val cardState = viewModel.cardIssuanceScreenState
        ScreenInternal(
            state = cardState,
            scrollState = scrollState,
            onGetXorClicked = viewModel::onGetXorClick,
        )
    }
}

@Composable
private fun ScreenInternal(
    state: CardIssuanceScreenState,
    scrollState: ScrollState,
    onGetXorClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        Text(
            text = stringResource(id = R.string.card_issuance_screen_title),
            style = MaterialTheme.customTypography.headline1,
            color = MaterialTheme.customColors.fgPrimary,
            modifier = Modifier.padding(start = Dimens.x2),
        )
        if (state.isScreenLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.customColors.fgPrimary,
                )
            }
        } else {
            FreeCardIssuance(state, onGetXorClicked)
            /* Will be available latter */
            // InlineTextDivider()
            // PaidCardIssuance(viewModel)
        }
    }
}

@Composable
private fun FreeCardIssuance(cardState: CardIssuanceScreenState, onClick: () -> Unit) {
    val state = cardState.freeCardIssuanceState

    ContentCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.x3, vertical = Dimens.x1)
            .padding(top = Dimens.x1, bottom = Dimens.x7),
    ) {
        Column {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.x3, vertical = Dimens.x2)
                    .padding(top = Dimens.x1),
                text = state.titleText.retrieveString(),
                style = MaterialTheme.customTypography.textLBold,
                color = MaterialTheme.customColors.fgPrimary,
                textAlign = TextAlign.Left,
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.x3)
                    .padding(bottom = Dimens.x2),
                text = state.descriptionText.retrieveString(),
                style = MaterialTheme.customTypography.textM,
                color = MaterialTheme.customColors.fgPrimary,
                textAlign = TextAlign.Left,
            )

            BalanceIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.x3)
                    .padding(bottom = Dimens.x2),
                percent = state.xorSufficiencyPercentage,
                label = state.xorSufficiencyText.retrieveString(),
            )

            FilledLargePrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.x3)
                    .padding(bottom = Dimens.x3),
                text = state.getInsufficientXorText,
                enabled = state.isGetInsufficientXorButtonEnabled,
                onClick = onClick,
            )
        }
    }
}

@Composable
private fun PaidCardIssuance(viewModel: CardIssuanceViewModel) {
    val state = viewModel.cardIssuanceScreenState.paidCardIssuanceState

    ContentCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.x3, vertical = Dimens.x1)
            .padding(bottom = Dimens.x7),
    ) {
        Column {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.x3, vertical = Dimens.x2)
                    .padding(top = Dimens.x1),
                text = state.titleText.retrieveString(),
                style = MaterialTheme.customTypography.textLBold,
                color = MaterialTheme.customColors.fgPrimary,
                textAlign = TextAlign.Left,
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.x3)
                    .padding(bottom = Dimens.x2),
                text = state.descriptionText.retrieveString(),
                style = MaterialTheme.customTypography.textM,
                color = MaterialTheme.customColors.fgPrimary,
                textAlign = TextAlign.Left,
            )

            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.x3)
                    .padding(bottom = Dimens.x3),
                text = state.payIssuanceAmountText.retrieveString(),
                order = Order.PRIMARY,
                size = Size.Large,
                enabled = state.isPayIssuanceAmountButtonEnabled,
                onClick = viewModel::onPayIssuance,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewCardIssuanceScreen() {
    AuthSdkTheme {
        ScreenInternal(
            state = CardIssuanceScreenState(
                screenStatus = ScreenStatus.READY_TO_RENDER,
                xorInsufficientAmount = 12.34,
                euroInsufficientAmount = 34.56,
                euroIssuanceAmount = "99.9",
                euroLiquidityThreshold = 222.2,
            ),
            scrollState = rememberScrollState(),
            onGetXorClicked = {},
        )
    }
}
