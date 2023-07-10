package jp.co.soramitsu.oauth.feature.verification.rejected

import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.paywings.onboarding.kyc.android.sdk.data.model.KycUserData
import com.paywings.onboarding.kyc.android.sdk.data.model.UserCredentials
import jp.co.soramitsu.oauth.common.interactors.account.api.AccountInteractor
import jp.co.soramitsu.oauth.common.interactors.account.api.AccountOperationResult
import jp.co.soramitsu.oauth.theme.views.Screen
import jp.co.soramitsu.oauth.theme.views.retrieveString
import jp.co.soramitsu.oauth.common.interactors.prices.api.PriceInteractor
import jp.co.soramitsu.oauth.common.interactors.prices.api.EuroLiquiditySufficiency
import jp.co.soramitsu.oauth.common.interactors.prices.api.XorLiquiditySufficiency
import jp.co.soramitsu.oauth.common.interactors.user.api.UserInteractor
import jp.co.soramitsu.oauth.common.interactors.user.api.UserOperationResult
import jp.co.soramitsu.oauth.common.navigation.flow.verification.api.VerificationDestination
import jp.co.soramitsu.oauth.common.navigation.flow.verification.api.VerificationFlow
import jp.co.soramitsu.oauth.core.engines.router.api.SoraCardDestinations
import jp.co.soramitsu.ui_core.component.button.FilledButton
import jp.co.soramitsu.ui_core.component.button.TonalButton
import jp.co.soramitsu.ui_core.component.button.properties.Order
import jp.co.soramitsu.ui_core.component.button.properties.Size
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow


@Composable
fun VerificationRejectedScreen(
    viewModel: VerificationRejectedViewModel = hiltViewModel()
) {
    Screen(
        viewModel = viewModel
    ) { scrollState ->
        VerificationRejectedContent(
            scrollState = scrollState,
            viewModel = viewModel,
        )
    }
}

@Composable
private fun VerificationRejectedContent(
    scrollState: ScrollState,
    viewModel: VerificationRejectedViewModel
) {
    val state = viewModel.verificationRejectedScreenState
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(top = Dimens.x3, start = Dimens.x3, end = Dimens.x3, bottom = Dimens.x5)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = state.descriptionText.retrieveString(),
            style = MaterialTheme.customTypography.paragraphM,
            color = MaterialTheme.customColors.fgPrimary
        )

        if (state.additionalInfo.isNotBlank())
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x3),
                text = state.additionalInfo,
                style = MaterialTheme.customTypography.paragraphM,
                color = MaterialTheme.customColors.fgPrimary
            )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(state.imageRes),
                contentDescription = null
            )
        }

        if (state.shouldKycAttemptsLeftTextBeShown)
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x3),
                text = state.kycAttemptsLeftText.retrieveString(),
                style = MaterialTheme.customTypography.paragraphMBold,
                color = MaterialTheme.customColors.fgPrimary,
                textAlign = TextAlign.Center
            )

        if (state.shouldKycAttemptsDisclaimerTextBeShown)
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x1_2),
                text = state.kycAttemptsDisclaimerText.retrieveString(),
                style = MaterialTheme.customTypography.paragraphM,
                color = MaterialTheme.customColors.fgPrimary,
                textAlign = TextAlign.Center
            )

        if (state.shouldTryAgainButtonBeShown)
            FilledButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x3),
                order = Order.SECONDARY,
                size = Size.Large,
                text = state.tryAgainText.retrieveString(),
                onClick = viewModel::onTryAgain
            )

        TonalButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.x3),
            order = Order.SECONDARY,
            size = Size.Large,
            text = state.telegramSupportText.retrieveString(),
            onClick = viewModel::openTelegramSupport
        )
    }
}

@Composable
@Preview
private fun PreviewApplicationRejected() {
    VerificationRejectedScreen(
        viewModel = VerificationRejectedViewModel(
            userInteractor = object : UserInteractor {
                override val resultFlow: StateFlow<UserOperationResult>
                    get() = TODO("Not yet implemented")

                override suspend fun getUserData() {
                    TODO("Not yet implemented")
                }

                override suspend fun calculateFreeKycAttemptsLeft(): Result<Int> {
                    TODO("Not yet implemented")
                }
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
            },
            verificationFlow = object : VerificationFlow {
                override val argsFlow: SharedFlow<Pair<SoraCardDestinations, Bundle>>
                    get() = TODO("Not yet implemented")

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

                override fun onLogout() {
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
            },
            accountInteractor = object : AccountInteractor {
                override val resultFlow: SharedFlow<AccountOperationResult>
                    get() = TODO("Not yet implemented")

                override suspend fun checkKycVerificationStatus() {
                    TODO("Not yet implemented")
                }

                override suspend fun requestOtpCode(phoneNumber: String) {
                    TODO("Not yet implemented")
                }

                override suspend fun resendOtpCode() {
                    TODO("Not yet implemented")
                }

                override suspend fun verifyOtpCode(otpCode: String) {
                    TODO("Not yet implemented")
                }

                override suspend fun registerUser(
                    firstName: String,
                    lastName: String,
                    email: String
                ) {
                    TODO("Not yet implemented")
                }

                override suspend fun checkEmailVerificationStatus() {
                    TODO("Not yet implemented")
                }

                override suspend fun requestNewVerificationEmail() {
                    TODO("Not yet implemented")
                }

                override suspend fun changeUnverifiedEmail(newEmail: String) {
                    TODO("Not yet implemented")
                }

                override suspend fun logOut() {
                    TODO("Not yet implemented")
                }
            }
        )
    )
}
