package jp.co.soramitsu.oauth.feature.registration.email

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.Screen
import jp.co.soramitsu.oauth.feature.OAuthCallback
import jp.co.soramitsu.oauth.feature.verify.VerifyUserData

@Composable
fun EnterEmailScreen(
    firstName: String?,
    lastName: String?,
    viewModel: EnterEmailViewModel = hiltViewModel()
) {
    Screen(
        viewModel = viewModel
    ) { scrollState ->
        val state = viewModel.state

        VerifyUserData(
            scrollState = scrollState,
            title = stringResource(R.string.enter_email_description),
            inputTextState = state.inputTextState,
            buttonState = state.buttonState,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            onDataEntered = viewModel::onEmailChanged,
            onConfirm = viewModel::onRegisterUser
        )
    }
}
