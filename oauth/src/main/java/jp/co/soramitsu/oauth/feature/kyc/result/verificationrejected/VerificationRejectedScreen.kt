package jp.co.soramitsu.oauth.feature.kyc.result.verificationrejected

import android.app.Activity
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
import androidx.navigation.NavHostController
import jp.co.soramitsu.oauth.base.compose.Screen
import jp.co.soramitsu.oauth.base.compose.retrieveString
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.domain.PriceInteractor
import jp.co.soramitsu.oauth.common.model.EuroLiquiditySufficiency
import jp.co.soramitsu.oauth.common.model.KycAttemptsDto
import jp.co.soramitsu.oauth.common.model.XorEuroPrice
import jp.co.soramitsu.oauth.common.model.XorLiquiditySufficiency
import jp.co.soramitsu.oauth.common.navigation.engine.activityresult.api.SetActivityResult
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.oauth.feature.terms.and.conditions.model.WebUrl
import jp.co.soramitsu.ui_core.component.button.FilledButton
import jp.co.soramitsu.ui_core.component.button.TonalButton
import jp.co.soramitsu.ui_core.component.button.properties.Order
import jp.co.soramitsu.ui_core.component.button.properties.Size
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography


@Composable
fun VerificationRejectedScreen(
    viewModel: VerificationRejectedViewModel = hiltViewModel(),
    additionalDescription: String? = null
) {
    Screen(
        viewModel = viewModel
    ) { scrollState ->
        VerificationRejectedContent(
            scrollState = scrollState,
            additionalDescription = additionalDescription,
            viewModel = viewModel,
        )
    }
}

@Composable
private fun VerificationRejectedContent(
    scrollState: ScrollState,
    additionalDescription: String?,
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

        if (additionalDescription != null)
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x3),
                text = additionalDescription,
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
            mainRouter = object : MainRouter {
                override fun attachNavController(
                    activity: Activity,
                    navHostController: NavHostController
                ) {
                    TODO("Not yet implemented")
                }

                override fun detachNavController(
                    activity: Activity,
                    navHostController: NavHostController
                ) {
                    TODO("Not yet implemented")
                }

                override fun back() {
                    TODO("Not yet implemented")
                }

                override fun openGetPrepared() {
                    TODO("Not yet implemented")
                }

                override fun openEnterPhoneNumber(clearStack: Boolean) {
                    TODO("Not yet implemented")
                }

                override fun openVerifyPhoneNumber(phoneNumber: String, otpLength: Int) {
                    TODO("Not yet implemented")
                }

                override fun openRegisterUser() {
                    TODO("Not yet implemented")
                }

                override fun openEnterEmail(firstName: String, lastName: String) {
                    TODO("Not yet implemented")
                }

                override fun openVerifyEmail(
                    email: String,
                    autoEmailSent: Boolean,
                    clearStack: Boolean
                ) {
                    TODO("Not yet implemented")
                }

                override fun openWebPage(titleRes: Int, url: WebUrl) {
                    TODO("Not yet implemented")
                }

                override fun openChangeEmail() {
                    TODO("Not yet implemented")
                }

                override fun openVerificationSuccessful() {
                    TODO("Not yet implemented")
                }

                override fun openVerificationInProgress() {
                    TODO("Not yet implemented")
                }

                override fun openVerificationFailed(additionalDescription: String?) {
                    TODO("Not yet implemented")
                }

                override fun openVerificationRejected(additionalDescription: String?) {
                    TODO("Not yet implemented")
                }

                override fun openSupportChat() {
                    TODO("Not yet implemented")
                }

                override fun navigate(destinationRoute: String) {
                    TODO("Not yet implemented")
                }

                override fun popUpToAndNavigate(popUpRoute: String, destinationRoute: String) {
                    TODO("Not yet implemented")
                }

                override fun popUpTo(destinationRoute: String) {
                    TODO("Not yet implemented")
                }
            },
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

                override suspend fun getFreeKycAttemptsInfo(accessToken: String): Result<KycAttemptsDto> {
                    TODO("Not yet implemented")
                }

                override suspend fun getCurrentXorEuroPrice(accessToken: String): Result<XorEuroPrice> {
                    TODO("Not yet implemented")
                }
            },
            setActivityResult = object : SetActivityResult {
                override fun setResult(soraCardResult: SoraCardResult) {
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
            }
        )
    )
}
