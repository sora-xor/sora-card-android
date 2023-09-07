package jp.co.soramitsu.oauth.feature.verify.phone

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
        val state = viewModel.state.collectAsStateWithLifecycle()

        VerifyUserData(
            scrollState = scrollState,
            title = stringResource(R.string.enter_phone_number_description),
            inputTextState = state.value.inputTextState,
            buttonState = state.value.buttonState,
            inputVisualTransformation = state.value.phoneVisualTransformation,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onDataEntered = viewModel::onPhoneChanged,
            onConfirm = viewModel::onRequestCode,
        )
    }
}
