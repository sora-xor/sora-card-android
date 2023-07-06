package jp.co.soramitsu.oauth.feature.registration.sendverificationemail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.theme.views.Screen
import jp.co.soramitsu.oauth.theme.views.toTitle
import jp.co.soramitsu.ui_core.component.button.LoaderWrapper
import jp.co.soramitsu.ui_core.component.button.TextButton
import jp.co.soramitsu.ui_core.component.button.TonalButton
import jp.co.soramitsu.ui_core.component.button.properties.Order
import jp.co.soramitsu.ui_core.component.button.properties.Size
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

@Composable
fun VerifyEmailScreen(
    email: String,
    autoEmailSent: Boolean,
    viewModel: VerifyEmailViewModel = hiltViewModel(),
) {
    Screen(
        viewModel = viewModel
    ) { scrollState ->
        val state = viewModel.state

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = Dimens.x1, start = Dimens.x2, end = Dimens.x2, bottom = Dimens.x5)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.x8),
                text = stringResource(R.string.verify_email_description, email),
                style = MaterialTheme.customTypography.paragraphM,
                color = MaterialTheme.customColors.fgPrimary,
                textAlign = TextAlign.Center
            )

            LoaderWrapper(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x1),
                loaderSize = Size.Large,
                loading = state.resendLinkButtonState.loading,
            ) { modifier, _ ->
                TonalButton(
                    modifier = modifier,
                    order = Order.SECONDARY,
                    size = Size.Large,
                    text = state.resendLinkButtonState.timer.takeIf { it != null } ?: state.resendLinkButtonState.title.toTitle(),
                    enabled = state.resendLinkButtonState.enabled,
                    onClick = viewModel::onResendLink
                )
            }

            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x2),
                order = Order.SECONDARY,
                size = Size.Large,
                text = state.changeEmailButtonState.title.toTitle(),
                enabled = state.changeEmailButtonState.enabled,
                onClick = viewModel::onChangeEmail
            )
        }
    }
}