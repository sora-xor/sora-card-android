package jp.co.soramitsu.oauth.feature.verify.phone.uiscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.feature.OAuthCallback
import jp.co.soramitsu.oauth.feature.verify.VerifyUserDataScreen
import jp.co.soramitsu.oauth.feature.verify.phone.VerifyPhoneNumberViewModel
import jp.co.soramitsu.oauth.uiscreens.compose.Screen
import jp.co.soramitsu.oauth.uiscreens.compose.maskFilter
import jp.co.soramitsu.ui_core.theme.customColors

@Composable
fun VerifyPhoneNumberScreen(
    countryCode: String?,
    phoneNumber: String?,
    otpLength: Int?,
    authCallback: OAuthCallback,
    viewModel: VerifyPhoneNumberViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.setArgs(countryCode, phoneNumber, otpLength, authCallback)
    }

    Screen(
        viewModel = viewModel,
    ) { scrollState ->
        val state = viewModel.state.collectAsStateWithLifecycle().value

        val phone = maskFilter(AnnotatedString(countryCode.orEmpty() + phoneNumber.orEmpty())).text

        val focusRequester = remember { FocusRequester() }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.customColors.bgSurface),
        ) {
            VerifyUserDataScreen(
                scrollState = scrollState,
                title = stringResource(R.string.verify_phone_number_description, phone.toString()),
                inputTextState = state.inputTextState,
                buttonState = state.buttonState,
                focusRequester = focusRequester,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onDataEntered = viewModel::onCodeChanged,
                onConfirm = viewModel::resendOtp,
            )
        }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}
