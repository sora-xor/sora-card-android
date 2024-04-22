package jp.co.soramitsu.oauth.feature.gatehub

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jp.co.soramitsu.androidfoundation.format.EURO_SIGN
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.androidfoundation.format.retrieveString
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.Screen
import jp.co.soramitsu.ui_core.component.button.FilledButton
import jp.co.soramitsu.ui_core.component.button.properties.Order
import jp.co.soramitsu.ui_core.component.button.properties.Size
import jp.co.soramitsu.ui_core.component.card.ContentCard
import jp.co.soramitsu.ui_core.component.checkbox.CheckboxButton
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.borderRadius
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

data class GatehubOnboardingStep1State(
    val buttonEnabled: Boolean,
    val amounts: List<TextValue>,
    val selectedPos: Int? = null,
)

@Composable
fun GatehubOnboardingStep1Screen(viewModel: GatehubOnboardingStep1ViewModel = hiltViewModel()) {
    BackHandler {
        viewModel.onToolbarNavigation()
    }
    Screen(viewModel = viewModel) {
        GatehubOnboardingStep1ScreenContent(
            scrollState = it,
            state = viewModel.state.collectAsStateWithLifecycle().value,
            onNext = viewModel::onNext,
            onItemClick = viewModel::onItemSelected,
        )
    }
}

@Composable
private fun GatehubOnboardingStep1ScreenContent(
    scrollState: ScrollState,
    state: GatehubOnboardingStep1State,
    onNext: () -> Unit,
    onItemClick: (Int) -> Unit,
) {
    ContentCard(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        cornerRadius = MaterialTheme.borderRadius.s,
        innerPadding = PaddingValues(Dimens.x3),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .verticalScroll(scrollState),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(R.string.expected_volume),
                style = MaterialTheme.customTypography.paragraphM,
                color = MaterialTheme.customColors.fgPrimary,
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x2, bottom = Dimens.x1),
                text = stringResource(R.string.select_one),
                style = MaterialTheme.customTypography.paragraphM,
                color = MaterialTheme.customColors.fgSecondary,
            )
            state.amounts.forEachIndexed { i, amount ->
                CheckboxButton(
                    isSelected = i == state.selectedPos,
                    text = amount.retrieveString(),
                    onClick = { onItemClick.invoke(i) },
                )
            }
            FilledButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x2),
                text = stringResource(id = R.string.common_next),
                order = Order.PRIMARY,
                size = Size.Large,
                enabled = state.buttonEnabled,
                onClick = onNext,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewGatehubOnboardingStep1ScreenContent() {
    GatehubOnboardingStep1ScreenContent(
        scrollState = rememberScrollState(),
        state = GatehubOnboardingStep1State(
            buttonEnabled = true,
            selectedPos = 1,
            amounts = listOf("23.0", "782.2").map { TextValue.SimpleText("$it$EURO_SIGN") },
        ),
        onNext = {},
        onItemClick = {},
    )
}
