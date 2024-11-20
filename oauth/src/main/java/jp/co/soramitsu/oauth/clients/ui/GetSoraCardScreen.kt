package jp.co.soramitsu.oauth.clients.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import jp.co.soramitsu.androidfoundation.compose.testTagAsId
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.styledui.FilledLargePrimaryButton
import jp.co.soramitsu.oauth.styledui.TextLargePrimaryButton
import jp.co.soramitsu.oauth.theme.AuthSdkTheme
import jp.co.soramitsu.ui_core.component.card.ContentCard
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

data class GetSoraCardState(
    val xorRatioAvailable: Boolean = false,
    val connection: Boolean = false,
    val applicationFee: String,
)

@Composable
fun GetSoraCardScreen(
    scrollState: ScrollState,
    state: GetSoraCardState,
    onBlackList: () -> Unit,
    onSignUp: () -> Unit,
    onLogIn: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = Dimens.x2)
            .padding(bottom = Dimens.x5),
    ) {
        ContentCard(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = MaterialTheme.customColors.bgPage,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dimens.x2),
            ) {
                SoraCardImage(
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.x2, start = Dimens.x1, end = Dimens.x1),
                    text = stringResource(R.string.details_title),
                    color = MaterialTheme.customColors.fgPrimary,
                    style = MaterialTheme.customTypography.headline2,
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.x2, start = Dimens.x1, end = Dimens.x1),
                    text = stringResource(R.string.details_description),
                    color = MaterialTheme.customColors.fgPrimary,
                    style = MaterialTheme.customTypography.paragraphM,
                )

                AnnualFee()

                FreeCardIssuance()

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.x2, start = Dimens.x1, end = Dimens.x1),
                    text = stringResource(R.string.unsupported_countries_disclaimer),
                    color = MaterialTheme.customColors.fgPrimary,
                    style = MaterialTheme.customTypography.paragraphXS.copy(
                        textAlign = TextAlign.Center,
                    ),
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTagAsId("SoraCardResidents", "")
                        .padding(start = Dimens.x1, end = Dimens.x1)
                        .clickable(onClick = onBlackList),
                    text = stringResource(R.string.unsupported_countries_link),
                    style = MaterialTheme.customTypography.paragraphXS.copy(
                        textAlign = TextAlign.Center,
                        textDecoration = TextDecoration.Underline,
                    ),
                    color = MaterialTheme.customColors.statusError,
                )
                FilledLargePrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTagAsId("SoraCardSignUp", "")
                        .padding(vertical = Dimens.x2, horizontal = Dimens.x1),
                    text = TextValue.StringRes(id = R.string.sign_up_sora_card),
                    enabled = state.xorRatioAvailable && state.connection,
                    onClick = onSignUp,
                )
                TextLargePrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTagAsId("SoraCardHaveCard", "")
                        .padding(horizontal = Dimens.x1),
                    enabled = state.xorRatioAvailable && state.connection,
                    text = TextValue.StringRes(id = R.string.details_already_have_card),
                    onClick = onLogIn,
                )
            }
        }
    }
}

@Composable
private fun AnnualFee() {
    ContentCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimens.x2, start = Dimens.x1, end = Dimens.x1),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(
                    top = Dimens.x2,
                    bottom = Dimens.x2,
                    start = Dimens.x3,
                    end = Dimens.x3,
                ),
            text = stringResource(R.string.details_annual_service_fee),
            color = MaterialTheme.customColors.fgPrimary,
            style = MaterialTheme.customTypography.textL,
        )
    }
}

@Composable
private fun FreeCardIssuance() {
    ContentCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimens.x2, start = Dimens.x1, end = Dimens.x1),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.x2, horizontal = Dimens.x3),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                text = stringResource(R.string.card_issuance_screen_free_card_title),
                color = MaterialTheme.customColors.fgPrimary,
                style = MaterialTheme.customTypography.textL,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewGetSoraCardScreen() {
    CompositionLocalProvider(
        localCompositionUiStyle provides UiStyle.SW,
    ) {
        AuthSdkTheme(darkTheme = true) {
            GetSoraCardScreen(
                scrollState = rememberScrollState(),
                state = GetSoraCardState(
                    applicationFee = "29",
                    connection = true,
                    xorRatioAvailable = true,
                ),
                {},
                {},
                {},
            )
        }
    }
}

@Preview
@Composable
private fun PreviewGetSoraCardScreen3() {
    CompositionLocalProvider(
        localCompositionUiStyle provides UiStyle.SW,
    ) {
        AuthSdkTheme(darkTheme = false) {
            GetSoraCardScreen(
                scrollState = rememberScrollState(),
                state = GetSoraCardState(
                    applicationFee = "29",
                    connection = true,
                    xorRatioAvailable = true,
                ),
                {},
                {},
                {},
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0x239854)
@Composable
private fun PreviewGetSoraCardScreen2() {
    CompositionLocalProvider(
        localCompositionUiStyle provides UiStyle.FW,
    ) {
        AuthSdkTheme {
            GetSoraCardScreen(
                scrollState = rememberScrollState(),
                state = GetSoraCardState(
                    applicationFee = "29",
                    connection = true,
                    xorRatioAvailable = true,
                ),
                {},
                {},
                {},
            )
        }
    }
}
