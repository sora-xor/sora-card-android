package jp.co.soramitsu.oauth.feature.verify.phone.uiscreens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.testTagAsId
import jp.co.soramitsu.oauth.feature.flagEmoji
import jp.co.soramitsu.oauth.feature.verify.model.ButtonState
import jp.co.soramitsu.oauth.feature.verify.phone.EnterPhoneNumberViewModel
import jp.co.soramitsu.oauth.uiscreens.clientsui.UiStyle
import jp.co.soramitsu.oauth.uiscreens.clientsui.localCompositionUiStyle
import jp.co.soramitsu.oauth.uiscreens.compose.Screen
import jp.co.soramitsu.oauth.uiscreens.styledui.FilledLargeSecondaryButton
import jp.co.soramitsu.oauth.uiscreens.styledui.fw.FearlessCorneredShape
import jp.co.soramitsu.ui_core.component.button.LoaderWrapper
import jp.co.soramitsu.ui_core.component.button.properties.Size
import jp.co.soramitsu.ui_core.component.input.InputText
import jp.co.soramitsu.ui_core.component.input.InputTextState
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.borderRadius
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

@Composable
fun EnterPhoneNumberScreen(code: String?, viewModel: EnterPhoneNumberViewModel = hiltViewModel()) {
    viewModel.setLocale(code)
    BackHandler {
        viewModel.onToolbarNavigation()
    }
    Screen(
        viewModel = viewModel,
    ) { scrollState ->
        val state = viewModel.state.collectAsStateWithLifecycle().value
        val focusRequester = remember { FocusRequester() }
        PhoneScreen(
            scrollState = scrollState,
            inputTextStatePhoneCode = state.inputTextStateCode,
            inputTextStatePhoneNumber = state.inputTextStateNumber,
            buttonState = state.buttonState,
            focusRequester = focusRequester,
            onDataEnteredPhoneNumber = viewModel::onPhoneChanged,
            countryName = state.countryName,
            countryCode = state.countryCode,
            countryLoading = state.countryLoading,
            onCountry = viewModel::onSelectCountry,
            onConfirm = viewModel::onRequestCode,
        )
        LaunchedEffect(state.countryLoading) {
            if (!state.countryLoading) {
                focusRequester.requestFocus()
            }
        }
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
    focusRequester: FocusRequester,
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
            val modifier = when (localCompositionUiStyle.current) {
                UiStyle.SW ->
                    Modifier
                        .testTagAsId("PhoneDialCodeInput")
                        .fillMaxWidth()

                UiStyle.FW ->
                    Modifier
                        .testTagAsId("PhoneDialCodeInput")
                        .clip(FearlessCorneredShape())
                        .fillMaxWidth()
            }

            Box(
                modifier = Modifier
                    .height(Dimens.InputHeight)
                    .border(
                        width = 1.dp,
                        shape = RoundedCornerShape(MaterialTheme.borderRadius.ml),
                        color = MaterialTheme.customColors.fgOutline,
                    )
                    .weight(1f)
                    .clip(RoundedCornerShape(MaterialTheme.borderRadius.ml))
                    .clickable(
                        enabled = !countryLoading,
                        onClick = onCountry,
                    ),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    modifier = modifier
                        .padding(Dimens.x2),
                    text = inputTextStatePhoneCode.value.text,
                    style = MaterialTheme.customTypography.textM,
                    color = MaterialTheme.customColors.fgPrimary,
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
                    focusRequester = focusRequester,
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
            FilledLargeSecondaryButton(
                modifier = modifier.testTagAsId("PrimaryButton"),
                text = buttonState.timer.takeIf {
                    it != null
                }?.let { TextValue.SimpleText(it) } ?: buttonState.title,
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
        buttonState = ButtonState(TextValue.SimpleText("Title")),
        countryCode = "NZ",
        countryName = "New Zealand",
        focusRequester = remember { FocusRequester() },
        countryLoading = false,
        onCountry = {},
        onDataEnteredPhoneNumber = {},
        onConfirm = {},
    )
}
