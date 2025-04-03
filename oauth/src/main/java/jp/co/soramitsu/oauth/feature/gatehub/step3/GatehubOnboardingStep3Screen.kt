package jp.co.soramitsu.oauth.feature.gatehub.step3

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
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.androidfoundation.format.retrieveString
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.uiscreens.compose.Screen
import jp.co.soramitsu.oauth.uiscreens.styledui.FilledLargePrimaryButton
import jp.co.soramitsu.oauth.uiscreens.theme.AuthSdkTheme
import jp.co.soramitsu.ui_core.component.card.ContentCard
import jp.co.soramitsu.ui_core.component.checkbox.CheckboxButton
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.borderRadius
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

data class GatehubOnboardingStep3State(
    val buttonEnabled: Boolean,
    val sources: List<TextValue>,
    val selectedPos: List<Int>? = null,
)

@Composable
fun GatehubOnboardingStep3Screen(viewModel: GatehubOnboardingStep3ViewModel = hiltViewModel()) {
    BackHandler {
        viewModel.onToolbarNavigation()
    }
    Screen(viewModel = viewModel) {
        GatehubOnboardingStep3ScreenContent(
            scrollState = it,
            state = viewModel.state.collectAsStateWithLifecycle().value,
            onNext = viewModel::onNext,
            onItemClick = viewModel::onItemSelected,
        )
    }
}

@Composable
private fun GatehubOnboardingStep3ScreenContent(
    scrollState: ScrollState,
    state: GatehubOnboardingStep3State,
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
                text = stringResource(R.string.source_of_funds),
                style = MaterialTheme.customTypography.paragraphM,
                color = MaterialTheme.customColors.fgPrimary,
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x2, bottom = Dimens.x1),
                text = stringResource(R.string.select_many),
                style = MaterialTheme.customTypography.paragraphM,
                color = MaterialTheme.customColors.fgSecondary,
            )
            state.sources.forEachIndexed { i, amount ->
                CheckboxButton(
                    isSelected = state.selectedPos?.contains(i) == true,
                    text = amount.retrieveString(),
                    onClick = { onItemClick.invoke(i) },
                )
            }
            FilledLargePrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x2),
                text = TextValue.StringRes(id = R.string.common_done),
                enabled = state.buttonEnabled,
                onClick = onNext,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewGatehubOnboardingStep3ScreenContent() {
    AuthSdkTheme {
        GatehubOnboardingStep3ScreenContent(
            scrollState = rememberScrollState(),
            state = GatehubOnboardingStep3State(
                buttonEnabled = true,
                selectedPos = listOf(1, 2),
                sources = listOf(
                    "salary",
                    "savings",
                    "belongings",
                    "profits",
                ).map { TextValue.SimpleText(it) },
            ),
            onNext = {},
            onItemClick = {},
        )
    }
}
