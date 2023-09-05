package jp.co.soramitsu.oauth.feature.getprepared

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.Screen
import jp.co.soramitsu.oauth.feature.OAuthCallback
import jp.co.soramitsu.ui_core.component.button.FilledButton
import jp.co.soramitsu.ui_core.component.button.properties.Order
import jp.co.soramitsu.ui_core.component.button.properties.Size
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.borderRadius
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

@Composable
fun GetPreparedScreen(
    authCallback: OAuthCallback,
    viewModel: GetPreparedViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.setArgs(authCallback)
    }

    BackHandler {
        viewModel.onToolbarNavigation()
    }

    Screen(
        viewModel = viewModel
    ) { scrollState ->
        val state = viewModel.state.collectAsStateWithLifecycle()
        GetPreparedScreenContent(
            scrollState,
            state.value,
            onConfirm = viewModel::onConfirm
        )
    }
}

@Composable
private fun GetPreparedScreenContent(
    scrollState: ScrollState,
    state: GetPreparedState,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(start = Dimens.x3, end = Dimens.x3, top = Dimens.x1, bottom = Dimens.x5),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimens.x4)
                .clip(RoundedCornerShape(MaterialTheme.borderRadius.s))
                .background(MaterialTheme.customColors.accentTertiaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.x2),
                text = stringResource(R.string.get_prepared_alert_dynamic, state.totalFreeAttemptsCount),
                style = MaterialTheme.customTypography.paragraphM,
                color = MaterialTheme.customColors.accentTertiary
            )
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimens.x4),
            text = stringResource(id = R.string.get_prepared_need),
            style = MaterialTheme.customTypography.paragraphM,
            color = MaterialTheme.customColors.fgPrimary
        )

        state.steps.forEach {
            Step(it)
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        FilledButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.x1),
            text = stringResource(id = R.string.get_prepared_ok_title),
            order = Order.SECONDARY,
            size = Size.Large,
            onClick = onConfirm
        )
    }
}

@Composable
private fun StepIcon(modifier: Modifier = Modifier, index: Int) {
    Box(
        modifier = modifier
            .size(Dimens.x6)
            .clip(CircleShape)
            .background(MaterialTheme.customColors.bgPage),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = index.toString(),
            style = MaterialTheme.customTypography.headline2,
            color = MaterialTheme.customColors.fgPrimary
        )
    }
}

@Composable
private fun Step(step: Step) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Dimens.x3),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StepIcon(
            modifier = Modifier.padding(end = Dimens.x2),
            index = step.index
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.x1_2),
                text = stringResource(step.title),
                style = MaterialTheme.customTypography.headline3,
                color = MaterialTheme.customColors.fgPrimary
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = step.description.map { stringResource(id = it) }.joinToString("\n\n"),
                style = MaterialTheme.customTypography.paragraphS,
                color = MaterialTheme.customColors.fgSecondary
            )
        }
    }
}


@Preview
@Composable
private fun PreviewGetPreparedScreen() {
    GetPreparedScreenContent(
        scrollState = rememberScrollState(),
        state = GetPreparedState(
            totalFreeAttemptsCount = "4",
            steps = listOf(
                Step(
                    index = 1,
                    title = R.string.get_prepared_submit_id_photo_title,
                    description = listOf(R.string.get_prepared_submit_id_photo_description),
                ),
                Step(
                    index = 2,
                    title = R.string.get_prepared_take_selfie_title,
                    description = listOf(R.string.get_prepared_take_selfie_description),
                ),
                Step(
                    index = 3,
                    title = R.string.get_prepared_proof_address_title,
                    description = listOf(R.string.get_prepared_proof_address_description, R.string.get_prepared_proof_address_note),
                ),
                Step(
                    index = 4,
                    title = R.string.get_prepared_personal_info_title,
                    description = listOf(R.string.get_prepared_personal_info_description),
                ),
            )
        ),
        onConfirm = {}
    )
}