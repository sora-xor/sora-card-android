package jp.co.soramitsu.oauth.feature.verification.result.cardissuance

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import jp.co.soramitsu.oauth.base.compose.BalanceIndicator
import jp.co.soramitsu.oauth.base.compose.InlineTextDivider
import jp.co.soramitsu.oauth.base.compose.Screen
import jp.co.soramitsu.oauth.base.compose.retrieveString
import jp.co.soramitsu.oauth.common.navigation.activityresult.api.SoraCardResult
import jp.co.soramitsu.oauth.common.interactors.prices.api.PriceInteractor
import jp.co.soramitsu.oauth.common.interactors.prices.api.EuroLiquiditySufficiency
import jp.co.soramitsu.oauth.common.interactors.prices.api.XorLiquiditySufficiency
import jp.co.soramitsu.oauth.common.navigation.activityresult.api.ActivityResult
import jp.co.soramitsu.oauth.common.navigation.flow.api.NavigationFlow
import jp.co.soramitsu.ui_core.component.button.FilledButton
import jp.co.soramitsu.ui_core.component.button.OutlinedButton
import jp.co.soramitsu.ui_core.component.button.properties.Order
import jp.co.soramitsu.ui_core.component.button.properties.Size
import jp.co.soramitsu.ui_core.component.card.ContentCard
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

@Composable
fun CardIssuanceScreen(
    viewModel: CardIssuanceViewModel = hiltViewModel()
) {
    Screen(
        viewModel = viewModel
    ) { scrollState ->
        if (viewModel.cardIssuanceScreenState.isScreenLoading) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.customColors.fgPrimary
                )
            }
        } else {
            Column(
                modifier = Modifier.verticalScroll(scrollState)
            ) {
                FreeCardIssuance(viewModel)
                InlineTextDivider()
                PaidCardIssuance(viewModel)
            }
        }
    }
}

@Composable
private fun FreeCardIssuance(
    viewModel: CardIssuanceViewModel
) {
    val state = viewModel.cardIssuanceScreenState.freeCardIssuanceState

    ContentCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.x3, vertical = Dimens.x1)
            .padding(top = Dimens.x1)
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
                textAlign = TextAlign.Left
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.x3)
                    .padding(bottom = Dimens.x2),
                text = state.descriptionText.retrieveString(),
                style = MaterialTheme.customTypography.textM,
                color = MaterialTheme.customColors.fgPrimary,
                textAlign = TextAlign.Left
            )

            BalanceIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.x3)
                    .padding(bottom = Dimens.x2),
                percent = state.xorSufficiencyPercentage,
                label = state.xorSufficiencyText.retrieveString(),
            )

            FilledButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.x3)
                    .padding(bottom = Dimens.x3),
                text = state.getInsufficientXorText.retrieveString(),
                order = Order.PRIMARY,
                size = Size.Large,
                enabled = state.isGetInsufficientXorButtonEnabled,
                onClick = viewModel::onGetXorClick
            )
        }
    }
}

@Composable
private fun PaidCardIssuance(
    viewModel: CardIssuanceViewModel
) {
    val state = viewModel.cardIssuanceScreenState.paidCardIssuanceState

    ContentCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.x3, vertical = Dimens.x1)
            .padding(bottom = Dimens.x7)
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
                textAlign = TextAlign.Left
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.x3)
                    .padding(bottom = Dimens.x2),
                text = state.descriptionText.retrieveString(),
                style = MaterialTheme.customTypography.textM,
                color = MaterialTheme.customColors.fgPrimary,
                textAlign = TextAlign.Left
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
                onClick = viewModel::onPayIssuance
            )
        }
    }
}

@Preview
@Composable
private fun PreviewCardIssuanceScreen() {
    CardIssuanceScreen(viewModel = CardIssuanceViewModel(
        kycRequirementsUnfulfilledFlow = object : NavigationFlow {
            override fun check() {}

            override fun start() {}

            override fun proceed() {}

            override fun back() {}

            override fun exit() {}
        },
        setActivityResult = object : ActivityResult {
            override fun setResult(soraCardResult: SoraCardResult) {}
        },
        priceInteractor = object : PriceInteractor {
            override suspend fun calculateXorLiquiditySufficiency(): Result<XorLiquiditySufficiency> {
                TODO("Not yet implemented")
            }

            override suspend fun calculateEuroLiquiditySufficiency(): Result<EuroLiquiditySufficiency> {
                TODO("Not yet implemented")
            }

            override suspend fun calculateCardIssuancePrice(): Result<Double> {
                TODO("Not yet implemented")
            }

            override suspend fun calculateKycAttemptPrice(): Result<Double> {
                TODO("Not yet implemented")
            }

        }
    ))
}