package jp.co.soramitsu.oauth.feature.login.enterotp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.theme.views.Screen
import jp.co.soramitsu.oauth.theme.views.VerifyUserData

@Composable
fun VerifyPhoneNumberScreen(
    viewModel: VerifyPhoneNumberViewModel = hiltViewModel()
) {
    Screen(
        viewModel = viewModel
    ) { scrollState ->
        val state = viewModel.state

        val phone = remember {
            "Your phone"
        }

        VerifyUserData(
            scrollState = scrollState,
            title = stringResource(R.string.verify_phone_number_description, phone.toString()),
            inputTextState = state.inputTextState,
            buttonState = state.buttonState,
            onDataEntered = viewModel::onCodeChanged,
            onConfirm = viewModel::resendOtp
        )
    }
}
