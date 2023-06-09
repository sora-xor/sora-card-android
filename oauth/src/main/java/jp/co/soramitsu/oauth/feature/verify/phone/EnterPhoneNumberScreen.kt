package jp.co.soramitsu.oauth.feature.verify.phone

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.Screen
import jp.co.soramitsu.oauth.feature.verify.VerifyUserData

@Composable
fun EnterPhoneNumberScreen(
    viewModel: EnterPhoneNumberViewModel = hiltViewModel()
) {
    Screen(
        viewModel = viewModel
    ) { scrollState ->
        val state = viewModel.state

        VerifyUserData(
            scrollState = scrollState,
            title = stringResource(R.string.enter_phone_number_description),
            inputTextState = state.inputTextState,
            buttonState = state.buttonState,
            inputVisualTransformation = state.phoneVisualTransformation,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onDataEntered = viewModel::onPhoneChanged,
            onConfirm = viewModel::onRequestCode,
            testTagIdPrefix = "EnterPhoneNumber"
        )
    }
}
