package jp.co.soramitsu.oauth.feature.cardissuance

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import jp.co.soramitsu.oauth.base.compose.BalanceIndicator
import jp.co.soramitsu.oauth.base.compose.InlineTextDivider
import jp.co.soramitsu.oauth.base.compose.Screen
import jp.co.soramitsu.oauth.base.compose.retrieveString
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.model.KycCount
import jp.co.soramitsu.oauth.common.model.XorEuroPrice
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
fun CardIssuanceScreen(
    viewModel: CardIssuanceViewModel = hiltViewModel()
) {
    Screen(
        viewModel = viewModel
    ) { scrollState ->
        Column(
            modifier = Modifier.verticalScroll(scrollState)
        ) {
            FreeCardIssuance(viewModel)
            InlineTextDivider()
            PaidCardIssuance(viewModel)
        }
    }
}

@Composable
private fun FreeCardIssuance(
    viewModel: CardIssuanceViewModel
) {
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
                text = viewModel.freeCardIssuanceState.titleText.retrieveString(),
                style = MaterialTheme.customTypography.textLBold,
                color = MaterialTheme.customColors.fgPrimary,
                textAlign = TextAlign.Left
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.x3)
                    .padding(bottom = Dimens.x2),
                text = viewModel.freeCardIssuanceState.descriptionText.retrieveString(),
                style = MaterialTheme.customTypography.textM,
                color = MaterialTheme.customColors.fgPrimary,
                textAlign = TextAlign.Left
            )

            BalanceIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.x3)
                    .padding(bottom = Dimens.x2),
                percent = viewModel.freeCardIssuanceState.xorSufficiencyPercentage,
                label = viewModel.freeCardIssuanceState.xorSufficiencyText.retrieveString(),
            )

            if (viewModel.freeCardIssuanceState.shouldGetInsufficientXorButtonBeShown)
                FilledButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.x3)
                        .padding(bottom = Dimens.x3),
                    text = viewModel.freeCardIssuanceState.getInsufficientXorText.retrieveString(),
                    order = Order.PRIMARY,
                    size = Size.Large,
                    onClick = viewModel::onGetXorClick
                )
        }
    }
}

@Composable
private fun PaidCardIssuance(
    viewModel: CardIssuanceViewModel
) {
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
                text = viewModel.paidCardIssuanceState.titleText.retrieveString(),
                style = MaterialTheme.customTypography.textLBold,
                color = MaterialTheme.customColors.fgPrimary,
                textAlign = TextAlign.Left
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.x3)
                    .padding(bottom = Dimens.x2),
                text = viewModel.paidCardIssuanceState.descriptionText.retrieveString(),
                style = MaterialTheme.customTypography.textM,
                color = MaterialTheme.customColors.fgPrimary,
                textAlign = TextAlign.Left
            )

            if (viewModel.paidCardIssuanceState.shouldPayIssuanceAmountButtonBeEnabled)
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.x3)
                        .padding(bottom = Dimens.x3),
                    text = viewModel.paidCardIssuanceState.payIssuanceAmountText.retrieveString(),
                    order = Order.PRIMARY,
                    size = Size.Large,
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
            override fun start(fromDestination: NavigationFlowDestination) {}

            override fun proceed() {}

            override fun back() {}

            override fun exit() {}
        },
        setActivityResult = object : SetActivityResult {
            override fun setResult(soraCardResult: SoraCardResult) {}
        },
        inMemoryRepo = InMemoryRepo(),
        userSessionRepository = object : UserSessionRepository {
            override suspend fun getRefreshToken(): String {
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
                expirationTime: Long
            ) {
                TODO("Not yet implemented")
            }

            override suspend fun setNewAccessToken(accessToken: String, expirationTime: Long) {
                TODO("Not yet implemented")
            }

            override suspend fun setRefreshToken(refreshToken: String) {
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
        kycRepository = object : KycRepository {
            override suspend fun getReferenceNumber(
                accessToken: String,
                phoneNumber: String?,
                email: String?
            ): Result<String> {
                TODO("Not yet implemented")
            }

            override suspend fun getKycLastFinalStatus(accessToken: String): Result<SoraCardCommonVerification?> {
                TODO("Not yet implemented")
            }

            override suspend fun hasFreeKycAttempt(accessToken: String): Result<Boolean> {
                TODO("Not yet implemented")
            }

            override suspend fun getFreeKycAttemptsInfo(accessToken: String): Result<KycCount> {
                TODO("Not yet implemented")
            }

            override suspend fun getCurrentXorEuroPrice(accessToken: String): Result<XorEuroPrice> {
                TODO("Not yet implemented")
            }
        }
    ))
}