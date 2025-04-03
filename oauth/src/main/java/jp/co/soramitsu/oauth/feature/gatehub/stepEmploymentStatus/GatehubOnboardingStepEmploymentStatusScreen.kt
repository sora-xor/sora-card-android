package jp.co.soramitsu.oauth.feature.gatehub.stepEmploymentStatus

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

internal data class GatehubOnboardingStepEmploymentStatusState(
    val buttonEnabled: Boolean,
    val statuses: List<TextValue>,
    val selectedPos: Int?,
)

@Composable
fun GatehubOnboardingStepEmploymentStatusScreen(
    modifier: Modifier = Modifier,
    viewModel: GatehubOnboardingStepEmploymentStatusViewModel = hiltViewModel(),
) {
    BackHandler {
        viewModel.onToolbarNavigation()
    }
    Screen(
        viewModel = viewModel,
    ) {
        GatehubOnboardingStepEmploymentStatusScreenContent(
            modifier = modifier,
            scrollState = it,
            state = viewModel.state.collectAsStateWithLifecycle().value,
            onNext = viewModel::onNext,
            onItemClick = viewModel::onItemSelect,
        )
    }
}

@Composable
private fun GatehubOnboardingStepEmploymentStatusScreenContent(
    modifier: Modifier = Modifier,
    scrollState: ScrollState,
    state: GatehubOnboardingStepEmploymentStatusState,
    onNext: () -> Unit,
    onItemClick: (Int) -> Unit,
) {
    ContentCard(
        modifier = modifier
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
                text = stringResource(R.string.gatehub_employment_status),
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
            state.statuses.forEachIndexed { index, textValue ->
                CheckboxButton(
                    isSelected = index == state.selectedPos,
                    text = textValue.retrieveString(),
                    onClick = { onItemClick.invoke(index) },
                )
            }

            FilledLargePrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x2),
                text = TextValue.StringRes(id = R.string.common_next),
                enabled = state.buttonEnabled,
                onClick = onNext,
            )
        }
    }
}

@Preview
@Composable
private fun GatehubOnboardingStepEmploymentStatusScreenContentPreview() {
    AuthSdkTheme {
        GatehubOnboardingStepEmploymentStatusScreenContent(
            scrollState = rememberScrollState(),
            state = GatehubOnboardingStepEmploymentStatusState(
                buttonEnabled = true,
                statuses = listOf(
                    TextValue.SimpleText("Working"),
                    TextValue.SimpleText("Worked"),
                    TextValue.SimpleText("Not going"),
                    TextValue.SimpleText("I do my best"),
                ),
                selectedPos = 1,
            ),
            onNext = {},
            onItemClick = {},
        )
    }
}
