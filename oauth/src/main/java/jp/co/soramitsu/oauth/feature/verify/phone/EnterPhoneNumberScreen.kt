package jp.co.soramitsu.oauth.feature.verify.phone

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.Screen
import jp.co.soramitsu.oauth.base.compose.toTitle
import jp.co.soramitsu.oauth.base.extension.testTagAsId
import jp.co.soramitsu.oauth.feature.flagEmoji
import jp.co.soramitsu.oauth.feature.verify.model.ButtonState
import jp.co.soramitsu.ui_core.component.button.FilledButton
import jp.co.soramitsu.ui_core.component.button.LoaderWrapper
import jp.co.soramitsu.ui_core.component.button.properties.Order
import jp.co.soramitsu.ui_core.component.button.properties.Size
import jp.co.soramitsu.ui_core.component.input.InputText
import jp.co.soramitsu.ui_core.component.input.InputTextState
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

@Composable
fun EnterPhoneNumberScreen(code: String?, viewModel: EnterPhoneNumberViewModel = hiltViewModel()) {
    viewModel.setLocale(code)
    Screen(
        viewModel = viewModel,
    ) { scrollState ->
        val state = viewModel.state.collectAsStateWithLifecycle().value
        PhoneScreen(
            scrollState = scrollState,
            inputTextStatePhoneCode = state.inputTextStateCode,
            inputTextStatePhoneNumber = state.inputTextStateNumber,
            buttonState = state.buttonState,
            onDataEnteredPhoneCode = {},
            onDataEnteredPhoneNumber = viewModel::onPhoneChanged,
            countryName = state.countryName,
            countryCode = state.countryCode,
            countryLoading = state.countryLoading,
            onCountry = viewModel::onSelectCountry,
            onConfirm = viewModel::onRequestCode,
        )
    }
}

@Composable
private fun PhoneScreen(
    scrollState: ScrollState,
    inputTextStatePhoneCode: InputTextState,
    inputTextStatePhoneNumber: InputTextState,
    buttonState: ButtonState,
    countryCode: String,
    countryName: String,
    countryLoading: Boolean,
    onDataEnteredPhoneCode: (TextFieldValue) -> Unit,
    onDataEnteredPhoneNumber: (TextFieldValue) -> Unit,
    onCountry: () -> Unit,
    onConfirm: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(
                vertical = Dimens.x2,
                horizontal = Dimens.x3,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimens.x3),
            text = stringResource(R.string.enter_phone_number_description),
            style = MaterialTheme.customTypography.paragraphM,
            color = MaterialTheme.customColors.fgPrimary,
            textAlign = TextAlign.Center,
        )

        if (countryLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(Dimens.x3),
                color = MaterialTheme.customColors.fgPrimary,
            )
        } else {
            Row(
                modifier = Modifier
                    .padding(vertical = Dimens.x2)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clickable(onClick = onCountry),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(countryCode.flagEmoji())
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = Dimens.x1),
                    text = countryName,
                    style = MaterialTheme.customTypography.textM,
                    color = MaterialTheme.customColors.fgPrimary,
                )
                Icon(
                    modifier = Modifier
                        .size(Dimens.IconSizeLarge),
                    painter = painterResource(jp.co.soramitsu.ui_core.R.drawable.ic_arrow_right),
                    contentDescription = null,
                    tint = MaterialTheme.customColors.fgSecondary,
                )
            }
        }

        Row(
            modifier = Modifier
                .padding(vertical = Dimens.x2)
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .weight(1f),
            ) {
                InputText(
                    modifier = Modifier
                        .testTagAsId("PhoneDialCodeInput")
                        .fillMaxWidth(),
                    state = inputTextStatePhoneCode,
                    onValueChange = onDataEnteredPhoneCode,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
            Spacer(modifier = Modifier.size(Dimens.x1))
            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .weight(2f),
            ) {
                InputText(
                    modifier = Modifier
                        .testTagAsId("PhoneDialNumberInput")
                        .fillMaxWidth(),
                    state = inputTextStatePhoneNumber,
                    onValueChange = onDataEnteredPhoneNumber,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
        }

        LoaderWrapper(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.x3),
            loading = buttonState.loading,
            loaderSize = Size.Large,
        ) { modifier, _ ->
            FilledButton(
                modifier = modifier.testTagAsId("PrimaryButton"),
                order = Order.SECONDARY,
                size = Size.Large,
                text = buttonState.timer.takeIf { it != null } ?: buttonState.title.toTitle(),
                enabled = buttonState.enabled,
                onClick = onConfirm,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewScreen() {
    PhoneScreen(
        scrollState = rememberScrollState(),
        inputTextStatePhoneCode = InputTextState(TextFieldValue("code")),
        inputTextStatePhoneNumber = InputTextState(TextFieldValue("number")),
        buttonState = ButtonState("Title"),
        countryCode = "NZ",
        countryName = "New Zealand",
        countryLoading = false,
        onCountry = {},
        onDataEnteredPhoneCode = {},
        onDataEnteredPhoneNumber = {},
        onConfirm = {},
    )
}
