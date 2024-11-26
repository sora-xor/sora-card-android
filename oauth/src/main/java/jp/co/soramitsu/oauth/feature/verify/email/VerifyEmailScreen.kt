package jp.co.soramitsu.oauth.feature.verify.email

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.androidfoundation.format.retrieveString
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.feature.OAuthCallback
import jp.co.soramitsu.oauth.uiscreens.compose.Screen
import jp.co.soramitsu.oauth.uiscreens.styledui.LargeTonalButton
import jp.co.soramitsu.oauth.uiscreens.styledui.TextLargePrimaryButton
import jp.co.soramitsu.ui_core.component.button.LoaderWrapper
import jp.co.soramitsu.ui_core.component.button.properties.Size
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

@Composable
fun VerifyEmailScreen(
    email: String,
    autoEmailSent: Boolean,
    authCallback: OAuthCallback,
    viewModel: VerifyEmailViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.setArgs(email, autoEmailSent, authCallback)
    }

    Screen(
        viewModel = viewModel,
    ) { scrollState ->
        val state = viewModel.state

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.customColors.bgSurface)
                .verticalScroll(scrollState)
                .padding(top = Dimens.x1, start = Dimens.x2, end = Dimens.x2, bottom = Dimens.x5),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.x8),
                text = stringResource(R.string.verify_email_description, email),
                style = MaterialTheme.customTypography.paragraphM,
                color = MaterialTheme.customColors.fgPrimary,
                textAlign = TextAlign.Center,
            )

            LoaderWrapper(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x1),
                loaderSize = Size.Large,
                loading = state.resendLinkButtonState.loading,
            ) { modifier, _ ->
                LargeTonalButton(
                    text = TextValue.SimpleText(
                        state.resendLinkButtonState.timer.takeIf {
                            it != null
                        } ?: state.resendLinkButtonState.title.retrieveString(),
                    ),
                    enabled = state.resendLinkButtonState.enabled,
                    onClick = viewModel::onResendLink,
                )
            }

            TextLargePrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x2),
                text = state.changeEmailButtonState.title,
                enabled = state.changeEmailButtonState.enabled,
                onClick = viewModel::onChangeEmail,
            )
        }
    }
}
