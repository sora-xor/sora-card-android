package jp.co.soramitsu.oauth.feature.gatehub.step2

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
import jp.co.soramitsu.oauth.base.compose.Screen
import jp.co.soramitsu.oauth.styledui.FilledLargePrimaryButton
import jp.co.soramitsu.ui_core.component.card.ContentCard
import jp.co.soramitsu.ui_core.component.checkbox.CheckboxButton
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.borderRadius
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

data class GatehubOnboardingStep2State(
    val buttonEnabled: Boolean,
    val reasons: List<TextValue>,
    val selectedPos: List<Int>? = null,
)

@Composable
fun GatehubOnboardingStep2Screen(viewModel: GatehubOnboardingStep2ViewModel = hiltViewModel()) {
    BackHandler {
        viewModel.onToolbarNavigation()
    }
    Screen(viewModel = viewModel) {
        GatehubOnboardingStep2ScreenContent(
            scrollState = it,
            state = viewModel.state.collectAsStateWithLifecycle().value,
            onNext = viewModel::onNext,
            onItemClick = viewModel::onItemSelected,
        )
    }
}

@Composable
private fun GatehubOnboardingStep2ScreenContent(
    scrollState: ScrollState,
    state: GatehubOnboardingStep2State,
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
                text = stringResource(R.string.opening_reason),
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
            state.reasons.forEachIndexed { i, reason ->
                CheckboxButton(
                    isSelected = state.selectedPos?.contains(i) == true,
                    text = reason.retrieveString(),
                    onClick = { onItemClick.invoke(i) },
                )
            }
            FilledLargePrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x2),
                text = TextValue.StringRes(R.string.common_next),
                enabled = state.buttonEnabled,
                onClick = onNext,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewGatehubOnboardingStep2ScreenContent() {
    GatehubOnboardingStep2ScreenContent(
        scrollState = rememberScrollState(),
        state = GatehubOnboardingStep2State(
            buttonEnabled = true,
            selectedPos = listOf(0, 3),
            reasons = listOf("reason 1", "reason 2", "reason 3", "reason 4").map {
                TextValue.SimpleText(
                    it,
                )
            },
        ),
        onNext = {},
        onItemClick = {},
    )
}
