package jp.co.soramitsu.oauth.feature.kyc.result

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.Screen
import jp.co.soramitsu.ui_core.component.button.TonalButton
import jp.co.soramitsu.ui_core.component.button.properties.Order
import jp.co.soramitsu.ui_core.component.button.properties.Size
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

@Composable
fun VerificationFailedScreen(
    viewModel: VerificationFailedViewModel = hiltViewModel(),
    additionalDescription: String? = null,
) {
    BackHandler {
        viewModel.onClose()
    }

    Screen(
        viewModel = viewModel,
    ) { scrollState ->
        VerificationFailedContent(
            scrollState = scrollState,
            additionalDescription = additionalDescription,
            onClose = viewModel::onClose,
        )
    }
}

@Composable
private fun VerificationFailedContent(
    scrollState: ScrollState,
    additionalDescription: String?,
    onClose: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(top = Dimens.x3, start = Dimens.x3, end = Dimens.x3, bottom = Dimens.x5),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.verification_failed_description),
            style = MaterialTheme.customTypography.paragraphM,
            color = MaterialTheme.customColors.fgPrimary,
        )

        additionalDescription?.let {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x3),
                text = additionalDescription,
                style = MaterialTheme.customTypography.paragraphM,
                color = MaterialTheme.customColors.fgPrimary,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_verification_rejected),
                contentDescription = null,
            )
        }

        TonalButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.x3),
            order = Order.SECONDARY,
            size = Size.Large,
            text = stringResource(R.string.common_close),
            enabled = true,
            onClick = onClose,
        )
    }
}

@Composable
@Preview
private fun PreviewVerificationFailedContent() {
    VerificationFailedContent(
        scrollState = rememberScrollState(),
        additionalDescription = "PLACEHOLDER",
        onClose = {},
    )
}
