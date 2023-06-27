package jp.co.soramitsu.oauth.feature.kyc.result

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.Screen
import jp.co.soramitsu.ui_core.component.button.FilledButton
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
            tryAgainAvailable = viewModel.uiState,
            onTryAgain = viewModel::onTryAgain,
        )
    }
}

@Composable
private fun VerificationRejectedContent(
    scrollState: ScrollState,
    additionalDescription: String?,
    tryAgainAvailable: Boolean,
    onTryAgain: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(scrollState)
            .padding(top = Dimens.x3, start = Dimens.x3, end = Dimens.x3, bottom = Dimens.x5)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.verification_rejected_description),
            style = MaterialTheme.customTypography.paragraphM,
            color = MaterialTheme.customColors.fgPrimary
        )

        additionalDescription?.let {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x3),
                text = additionalDescription,
                style = MaterialTheme.customTypography.paragraphM,
                color = MaterialTheme.customColors.fgPrimary
            )
        }

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

        FilledButton(
            modifier = Modifier.fillMaxWidth().padding(top = Dimens.x3),
            order = Order.SECONDARY,
            size = Size.Large,
            text = stringResource(R.string.common_try_again),
            enabled = tryAgainAvailable,
            onClick = onTryAgain
        )
    }
}

@Composable
@Preview
private fun PreviewApplicationRejected() {
    VerificationRejectedContent(
        scrollState = rememberScrollState(),
        additionalDescription = "PLACEHOLDER",
        tryAgainAvailable = false,
        onTryAgain = {}
    )
}
