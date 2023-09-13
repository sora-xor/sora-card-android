package jp.co.soramitsu.oauth.feature.kyc.result.verificationrejected

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.Screen
import jp.co.soramitsu.oauth.base.compose.ScreenStatus
import jp.co.soramitsu.oauth.base.compose.retrieveString
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
    BackHandler {
        viewModel.onToolbarNavigation()
    }
    Screen(
        viewModel = viewModel
    ) { scrollState ->
        VerificationRejectedContent(
            scrollState = scrollState,
            additionalDescription = additionalDescription,
            state = viewModel.verificationRejectedScreenState.collectAsStateWithLifecycle().value,
            onTryAgain = viewModel::onTryAgain,
            onTelegramSupport = viewModel::openTelegramSupport,
        )
    }
}

@Composable
private fun VerificationRejectedContent(
    scrollState: ScrollState,
    additionalDescription: String?,
    state: VerificationRejectedScreenState,
    onTryAgain: () -> Unit,
    onTelegramSupport: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(top = Dimens.x3, start = Dimens.x3, end = Dimens.x3, bottom = Dimens.x5)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.verification_rejected_description),
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
                painter = painterResource(R.drawable.ic_verification_rejected),
                contentDescription = null
            )
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.x3),
            text = state.kycAttemptsLeftText.retrieveString(),
            style = MaterialTheme.customTypography.paragraphMBold,
            color = MaterialTheme.customColors.fgPrimary,
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.x1_2),
            text = stringResource(
                id = R.string.verification_rejected_screen_attempts_price_disclaimer,
                formatArgs = arrayOf(state.kycAttemptCostInEuros.toString()),
            ),
            style = MaterialTheme.customTypography.paragraphM,
            color = MaterialTheme.customColors.fgPrimary,
            textAlign = TextAlign.Center
        )

        FilledButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.x3),
            order = Order.SECONDARY,
            enabled = state.shouldTryAgainButtonBeEnabled,
            size = Size.Large,
            text = state.tryAgainText.retrieveString(),
            onClick = onTryAgain,
        )

        TonalButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.x3),
            order = Order.SECONDARY,
            size = Size.Large,
            text = stringResource(id = R.string.verification_rejected_screen_support_telegram),
            onClick = onTelegramSupport,
        )
    }
}

@Composable
@Preview(locale = "en")
private fun PreviewApplicationRejected1() {
    VerificationRejectedContent(
        scrollState = rememberScrollState(),
        additionalDescription = "description",
        state = VerificationRejectedScreenState(
            screenStatus = ScreenStatus.READY_TO_RENDER,
            kycFreeAttemptsCount = 5,
            isFreeAttemptsLeft = true,
            kycAttemptCostInEuros = 23.3,
        ),
        onTelegramSupport = {},
        onTryAgain = {},
    )
}

@Composable
@Preview
private fun PreviewApplicationRejected2() {
    VerificationRejectedContent(
        scrollState = rememberScrollState(),
        additionalDescription = "description",
        state = VerificationRejectedScreenState(
            screenStatus = ScreenStatus.READY_TO_RENDER,
            kycFreeAttemptsCount = 0,
            isFreeAttemptsLeft = false,
            kycAttemptCostInEuros = 23.3,
        ),
        onTelegramSupport = {},
        onTryAgain = {},
    )
}
