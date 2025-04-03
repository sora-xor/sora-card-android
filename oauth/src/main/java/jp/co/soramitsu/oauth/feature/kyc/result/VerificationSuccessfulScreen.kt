package jp.co.soramitsu.oauth.feature.kyc.result

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.feature.YourPhoneNumberText
import jp.co.soramitsu.oauth.uiscreens.compose.Screen
import jp.co.soramitsu.oauth.uiscreens.styledui.LargeTonalButton
import jp.co.soramitsu.oauth.uiscreens.theme.AuthSdkTheme
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

@Composable
fun VerificationSuccessfulScreen(viewModel: VerificationSuccessfulViewModel = hiltViewModel()) {
    BackHandler {
        viewModel.onClose()
    }
    Screen(
        viewModel = viewModel,
    ) { scrollState ->
        val state = viewModel.state.collectAsStateWithLifecycle()
        VerificationSuccessfulContent(
            scrollState = scrollState,
            phone = state.value,
            onClose = viewModel::onClose,
        )
    }
}

@Composable
private fun VerificationSuccessfulContent(
    scrollState: ScrollState,
    phone: String,
    onClose: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.customColors.bgSurface)
            .verticalScroll(scrollState)
            .padding(top = Dimens.x3, start = Dimens.x3, end = Dimens.x3, bottom = Dimens.x5),
    ) {
        YourPhoneNumberText(phone = phone)
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.x2),
            text = stringResource(R.string.verification_successful_description),
            style = MaterialTheme.customTypography.paragraphM,
            color = MaterialTheme.customColors.fgPrimary,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_verification_successful),
                contentDescription = null,
            )
        }
        LargeTonalButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.x3),
            text = TextValue.StringRes(R.string.common_close),
            enabled = true,
            onClick = onClose,
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewVerificationInProgressScreen() {
    AuthSdkTheme {
        VerificationSuccessfulContent(
            scrollState = rememberScrollState(),
            phone = "+123457669789",
            onClose = {},
        )
    }
}
