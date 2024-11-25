package jp.co.soramitsu.oauth.feature.verify.email

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.feature.OAuthCallback
import jp.co.soramitsu.oauth.feature.verify.VerifyUserDataScreen
import jp.co.soramitsu.oauth.uiscreens.compose.Screen
import jp.co.soramitsu.ui_core.theme.customColors

@Composable
fun EnterEmailScreen(
    firstName: String?,
    lastName: String?,
    viewModel: EnterEmailViewModel = hiltViewModel(),
    authCallback: OAuthCallback,
) {
    LaunchedEffect(Unit) {
        viewModel.setArgs(firstName, lastName, authCallback)
    }

    Screen(
        viewModel = viewModel,
    ) { scrollState ->
        val state = viewModel.state
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.customColors.bgSurface),
        ) {
            VerifyUserDataScreen(
                scrollState = scrollState,
                title = stringResource(R.string.enter_email_description),
                inputTextState = state.inputTextState,
                buttonState = state.buttonState,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                onDataEntered = viewModel::onEmailChanged,
                onConfirm = viewModel::onRegisterUser,
            )
        }
    }
}
