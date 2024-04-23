package jp.co.soramitsu.oauth.feature.registration

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import jp.co.soramitsu.androidfoundation.compose.toTitle
import jp.co.soramitsu.oauth.base.compose.Screen
import jp.co.soramitsu.ui_core.component.button.FilledButton
import jp.co.soramitsu.ui_core.component.button.LoaderWrapper
import jp.co.soramitsu.ui_core.component.button.properties.Order
import jp.co.soramitsu.ui_core.component.button.properties.Size
import jp.co.soramitsu.ui_core.component.input.InputText
import jp.co.soramitsu.ui_core.resources.Dimens

@Composable
fun RegisterUserScreen(viewModel: RegisterUserViewModel = hiltViewModel()) {
    Screen(
        viewModel = viewModel,
    ) { scrollState ->
        val state = viewModel.state

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(vertical = Dimens.x2, horizontal = Dimens.x3),
        ) {
            InputText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.x2),
                state = state.firstNameState,
                onValueChange = viewModel::onFirstNameChanged,
            )

            InputText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.x3),
                state = state.lastNameState,
                onValueChange = viewModel::onLastNameChanged,
            )

            LoaderWrapper(
                modifier = Modifier.fillMaxWidth(),
                loaderSize = Size.Large,
                loading = state.buttonState.loading,
            ) { modifier, _ ->
                FilledButton(
                    modifier = modifier,
                    order = Order.SECONDARY,
                    size = Size.Large,
                    text = state.buttonState.title.toTitle(),
                    enabled = state.buttonState.enabled,
                    onClick = viewModel::onConfirm,
                )
            }
        }
    }
}
