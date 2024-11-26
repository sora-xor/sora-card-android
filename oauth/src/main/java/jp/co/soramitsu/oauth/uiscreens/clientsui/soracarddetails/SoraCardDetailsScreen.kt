package jp.co.soramitsu.oauth.uiscreens.clientsui.soracarddetails

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.uiscreens.clientsui.UiStyle
import jp.co.soramitsu.oauth.uiscreens.clientsui.localCompositionUiStyle
import jp.co.soramitsu.oauth.uiscreens.theme.AuthSdkTheme
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors

data class SoraCardDetailsScreenState(
    val soraCardMainSoraContentCardState: SoraCardMainSoraContentCardState,
    val soraCardReferralBannerCardState: Boolean = false,
    val soraCardRecentActivitiesCardState: SoraCardRecentActivitiesCardState? = null,
    val soraCardIBANCardState: SoraCardIBANCardState? = null,
    val soraCardSettingsCard: SoraCardSettingsCardState? = null,
    val logoutDialog: Boolean,
    val fiatWalletDialog: Boolean,
)

interface SoraCardDetailsCallback {
    fun onReferralBannerClick()
    fun onCloseReferralBannerClick()
    fun onRecentActivityClick(position: Int)
    fun onShowMoreRecentActivitiesClick()
    fun onIbanCardShareClick()
    fun onIbanCardClick()
    fun onSettingsOptionClick(position: Int)
    fun onExchangeXorClick()
}

@Composable
fun SoraCardDetailsScreen(
    scrollState: ScrollState,
    soraCardDetailsScreenState: SoraCardDetailsScreenState,
    callback: SoraCardDetailsCallback,
) {
    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
            .padding(horizontal = Dimens.x2),
        verticalArrangement = Arrangement
            .spacedBy(Dimens.x2),
        horizontalAlignment = Alignment
            .CenterHorizontally,
    ) {
        SoraCardMainSoraContentCardScreen(
            soraCardMainSoraContentCardState = soraCardDetailsScreenState.soraCardMainSoraContentCardState,
            onExchangeXor = callback::onExchangeXorClick,
            onOptionsClick = { callback.onSettingsOptionClick(0) },
        )
        if (soraCardDetailsScreenState.soraCardReferralBannerCardState) {
            BasicBannerCardScreen(
                imageContent = R.drawable.sora_card_referral_banner,
                title = stringResource(id = R.string.refer_sora_card_bonus),
                description = "",
                button = stringResource(id = R.string.refer_and_earn),
                closeEnabled = false,
                callback = BasicBannerCardCallback(
                    onButtonClicked = callback::onReferralBannerClick,
                    onCloseCard = callback::onCloseReferralBannerClick,
                ),
            )
        }
        soraCardDetailsScreenState.soraCardRecentActivitiesCardState?.let { state ->
            if (state.data.isNotEmpty()) {
                SoraCardRecentActivitiesCardScreen(
                    soraCardRecentActivitiesCardState = state,
                    onListTileClick = callback::onRecentActivityClick,
                    onShowMoreClick = callback::onShowMoreRecentActivitiesClick,
                )
            }
        }

        soraCardDetailsScreenState.soraCardIBANCardState?.let { state ->
            SoraCardIBANCardScreen(
                soraCardIBANCardState = state,
                onShareClick = callback::onIbanCardShareClick,
                onCardClick = callback::onIbanCardClick,
            )
        }
        soraCardDetailsScreenState.soraCardSettingsCard?.let { state ->
            if (state.settings.isNotEmpty()) {
                SoraCardSettingsCardScreen(
                    state = state,
                    onItemClick = callback::onSettingsOptionClick,
                )
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.x5),
        )
    }
}

@Preview
@Composable
private fun PreviewSoraCardDetailsScreen() {
    CompositionLocalProvider(
        localCompositionUiStyle provides UiStyle.FW,
    ) {
        AuthSdkTheme {
            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.customColors.bgPage,
                    )
                    .fillMaxSize(),
            ) {
                SoraCardDetailsScreen(
                    scrollState = rememberScrollState(),
                    soraCardDetailsScreenState = SoraCardDetailsScreenState(
                        soraCardMainSoraContentCardState = SoraCardMainSoraContentCardState(
                            balance = "3665.50",
                            phone = "987654",
                            soraCardMenuActions = SoraCardMenuAction.entries,
                            canStartGatehubFlow = true,
                        ),
                        soraCardReferralBannerCardState = true,
                        soraCardRecentActivitiesCardState = SoraCardRecentActivitiesCardState(
                            data = listOf(),
                        ),
                        soraCardIBANCardState = SoraCardIBANCardState(
                            iban = "LT61 3250 0467 7252 5583",
                            closed = false,
                        ),
                        soraCardSettingsCard = SoraCardSettingsCardState(
                            soraCardSettingsOptions = SoraCardSettingsOption.entries,
                            phone = "123123",
                        ),
                        logoutDialog = false,
                        fiatWalletDialog = false,
                    ),
                    callback = object : SoraCardDetailsCallback {
                        override fun onReferralBannerClick() {
                            TODO("Not yet implemented")
                        }

                        override fun onCloseReferralBannerClick() {
                            TODO("Not yet implemented")
                        }

                        override fun onRecentActivityClick(position: Int) {
                            TODO("Not yet implemented")
                        }

                        override fun onShowMoreRecentActivitiesClick() {
                            TODO("Not yet implemented")
                        }

                        override fun onIbanCardShareClick() {
                            TODO("Not yet implemented")
                        }

                        override fun onIbanCardClick() {
                            TODO("Not yet implemented")
                        }

                        override fun onSettingsOptionClick(position: Int) {
                            TODO("Not yet implemented")
                        }

                        override fun onExchangeXorClick() {
                            TODO("Not yet implemented")
                        }
                    },
                )
            }
        }
    }
}
