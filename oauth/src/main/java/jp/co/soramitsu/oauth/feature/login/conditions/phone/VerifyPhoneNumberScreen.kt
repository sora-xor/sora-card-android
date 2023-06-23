package jp.co.soramitsu.oauth.feature.login.conditions.phone

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.hilt.navigation.compose.hiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.Screen
import jp.co.soramitsu.oauth.base.compose.maskFilter
import jp.co.soramitsu.oauth.feature.KycCallback
import jp.co.soramitsu.oauth.feature.OAuthCallback
import jp.co.soramitsu.oauth.feature.verify.VerifyUserData

@Composable
fun VerifyPhoneNumberScreen(
    phoneNumber: String?,
    otpLength: Int?,
    viewModel: VerifyPhoneNumberViewModel = hiltViewModel()
) {
    Screen(
        viewModel = viewModel
    ) { scrollState ->
        val state = viewModel.state

        val phone = phoneNumber?.let {
            maskFilter(AnnotatedString(it)).text
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
