package jp.co.soramitsu.oauth.feature.cardissuance

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.BalanceIndicator
import jp.co.soramitsu.oauth.base.compose.Screen
import jp.co.soramitsu.oauth.base.compose.retrieveString
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.common.domain.PriceInteractor
import jp.co.soramitsu.oauth.common.model.EuroLiquiditySufficiency
import jp.co.soramitsu.oauth.common.model.XorLiquiditySufficiency
import jp.co.soramitsu.oauth.common.navigation.engine.activityresult.api.SetActivityResult
import jp.co.soramitsu.oauth.common.navigation.flow.api.NavigationFlow
import jp.co.soramitsu.oauth.common.navigation.flow.api.destinations.NavigationFlowDestination
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.ui_core.component.button.FilledButton
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
            if (viewModel.cardIssuanceScreenState.isScreenLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.customColors.fgPrimary,
                    )
                }
            } else {
                FreeCardIssuance(viewModel)
                /* Will be available latter */
                // InlineTextDivider()
                // PaidCardIssuance(viewModel)
            }
        }
    }
}

@Composable
private fun FreeCardIssuance(viewModel: CardIssuanceViewModel) {
    val state = viewModel.cardIssuanceScreenState.freeCardIssuanceState

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

            FilledButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.x3)
                    .padding(bottom = Dimens.x3),
                text = state.getInsufficientXorText.retrieveString(),
                order = Order.PRIMARY,
                size = Size.Large,
                enabled = state.isGetInsufficientXorButtonEnabled,
                onClick = viewModel::onGetXorClick,
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
    CardIssuanceScreen(
        viewModel = CardIssuanceViewModel(
            userSessionRepository = object : UserSessionRepository {
                override suspend fun getRefreshToken(): String {
                    TODO("Not yet implemented")
                }

                override suspend fun getUser(): Triple<String, String, Long> {
                    TODO("Not yet implemented")
                }

                override suspend fun getKycStatus(): SoraCardCommonVerification? {
                    TODO("Not yet implemented")
                }

                override suspend fun setKycStatus(status: SoraCardCommonVerification) {
                    TODO("Not yet implemented")
                }

                override suspend fun getAccessToken(): String {
                    TODO("Not yet implemented")
                }

                override suspend fun getAccessTokenExpirationTime(): Long {
                    TODO("Not yet implemented")
                }

                override suspend fun signInUser(
                    refreshToken: String,
                    accessToken: String,
                    expirationTime: Long,
                ) {
                    TODO("Not yet implemented")
                }

                override suspend fun setNewAccessToken(accessToken: String, expirationTime: Long) {
                    TODO("Not yet implemented")
                }

                override suspend fun setUserId(userId: String?) {
                    TODO("Not yet implemented")
                }

                override suspend fun setPersonId(personId: String?) {
                    TODO("Not yet implemented")
                }

                override suspend fun getUserId(): String {
                    TODO("Not yet implemented")
                }

                override suspend fun getPersonId(): String {
                    TODO("Not yet implemented")
                }

                override suspend fun logOutUser() {
                    TODO("Not yet implemented")
                }
            },
            kycRequirementsUnfulfilledFlow = object : NavigationFlow {
                override fun start(fromDestination: NavigationFlowDestination) {}

                override fun proceed() {}

                override fun back() {}

                override fun exit() {}
            },
            setActivityResult = object : SetActivityResult {
                override fun setResult(soraCardResult: SoraCardResult) {}
            },
            priceInteractor = object : PriceInteractor {
                override suspend fun calculateXorLiquiditySufficiency(): Result<XorLiquiditySufficiency> {
                    TODO("Not yet implemented")
                }

                override suspend fun calculateEuroLiquiditySufficiency(): Result<EuroLiquiditySufficiency> {
                    TODO("Not yet implemented")
                }

                override suspend fun calculateCardIssuancePrice(): String {
                    TODO("Not yet implemented")
                }

                override suspend fun calculateKycAttemptPrice(): String {
                    TODO("Not yet implemented")
                }
            },
        ),
    )
}
