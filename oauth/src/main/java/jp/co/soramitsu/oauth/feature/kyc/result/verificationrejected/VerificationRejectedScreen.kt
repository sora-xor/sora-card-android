package jp.co.soramitsu.oauth.feature.kyc.result.verificationrejected

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.androidfoundation.format.retrieveString
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.feature.YourPhoneNumberText
import jp.co.soramitsu.oauth.uiscreens.compose.Screen
import jp.co.soramitsu.oauth.uiscreens.compose.ScreenStatus
import jp.co.soramitsu.oauth.uiscreens.styledui.FilledLargeSecondaryButton
import jp.co.soramitsu.oauth.uiscreens.styledui.LargeTonalButton
import jp.co.soramitsu.oauth.uiscreens.theme.AuthSdkTheme
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

@Composable
fun VerificationRejectedScreen(viewModel: VerificationRejectedViewModel = hiltViewModel()) {
    BackHandler {
        viewModel.onToolbarNavigation()
    }
    Screen(
        viewModel = viewModel,
    ) { scrollState ->
        VerificationRejectedContent(
            scrollState = scrollState,
            state = viewModel.verificationRejectedScreenState.collectAsStateWithLifecycle().value,
            onTryAgain = viewModel::onTryAgain,
            onTelegramSupport = viewModel::openTelegramSupport,
        )
    }
}

@Composable
private fun VerificationRejectedContent(
    scrollState: ScrollState,
    state: VerificationRejectedScreenState,
    onTryAgain: () -> Unit,
    onTelegramSupport: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.customColors.bgSurface)
            .padding(top = Dimens.x3, start = Dimens.x3, end = Dimens.x3, bottom = Dimens.x5),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        YourPhoneNumberText(phone = state.phone)
        Column(
            modifier = Modifier
                .padding(top = Dimens.x2)
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = state.reason
                    ?: stringResource(id = R.string.verification_rejected_description),
                style = MaterialTheme.customTypography.paragraphM,
                color = MaterialTheme.customColors.fgPrimary,
            )
            state.reasonDetails?.let { details ->
                Spacer(modifier = Modifier.size(Dimens.x2))
                details.forEach {
                    ReasonRow(reason = it)
                }
            }
        }

        Image(
            painter = painterResource(R.drawable.ic_verification_rejected),
            contentDescription = null,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x3),
                text = state.kycAttemptsLeftText.retrieveString(),
                style = MaterialTheme.customTypography.paragraphMBold,
                color = MaterialTheme.customColors.fgPrimary,
                textAlign = TextAlign.Center,
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x1_2),
                text = stringResource(
                    id = R.string.paid_attempts_available_later,
                    formatArgs = arrayOf(state.kycAttemptCostInEuros),
                ),
                style = MaterialTheme.customTypography.paragraphM,
                color = MaterialTheme.customColors.fgPrimary,
                textAlign = TextAlign.Center,
            )

            FilledLargeSecondaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x3),
                enabled = state.shouldTryAgainButtonBeEnabled,
                text = state.tryAgainText,
                onClick = onTryAgain,
            )

            LargeTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x3),
                enabled = true,
                text = TextValue.StringRes(
                    id = R.string.verification_rejected_screen_support_telegram,
                ),
                onClick = onTelegramSupport,
            )
        }
    }
}

@Composable
private fun ReasonRow(reason: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Text(
            modifier = Modifier.wrapContentSize(),
            text = "•",
            style = MaterialTheme.customTypography.paragraphM,
            color = MaterialTheme.customColors.fgPrimary,
        )
        Spacer(modifier = Modifier.size(Dimens.x1))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = reason,
            style = MaterialTheme.customTypography.paragraphM,
            color = MaterialTheme.customColors.fgPrimary,
        )
    }
}

@Composable
@Preview(locale = "en", showBackground = true)
private fun PreviewApplicationRejected1() {
    AuthSdkTheme {
        VerificationRejectedContent(
            scrollState = rememberScrollState(),
            state = VerificationRejectedScreenState(
                screenStatus = ScreenStatus.READY_TO_RENDER,
                kycFreeAttemptsCount = 5,
                isFreeAttemptsLeft = true,
                phone = "+876857464645634",
                kycAttemptCostInEuros = "3.80",
                reason = "Video was rejected",
                reasonDetails = listOf(
                    "The user uploaded screenshot, Why did he do it, Who knows.",
                    "The user uploaded screenshot, Why did he do it, Who knows.",
//                "The user uploaded screenshot, Why did he do it, Who knows.",
//                "The user uploaded screenshot, Why did he do it, Who knows.",
//                "Poor quality",
//                "Poor quality",
//                "Poor quality",
//                "Poor quality",
//                "Damaged",
//                "Damaged",
//                "Damaged",
                    "Damaged",
                ),
            ),
            onTelegramSupport = {},
            onTryAgain = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewApplicationRejected2() {
    AuthSdkTheme {
        VerificationRejectedContent(
            scrollState = rememberScrollState(),
            state = VerificationRejectedScreenState(
                screenStatus = ScreenStatus.READY_TO_RENDER,
                kycFreeAttemptsCount = 0,
                isFreeAttemptsLeft = false,
                kycAttemptCostInEuros = "3.80",
                reason = null,
                phone = "+876857464645634",
                reasonDetails = null,
            ),
            onTelegramSupport = {},
            onTryAgain = {},
        )
    }
}
