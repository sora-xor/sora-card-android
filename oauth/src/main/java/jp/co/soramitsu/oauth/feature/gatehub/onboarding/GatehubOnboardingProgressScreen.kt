package jp.co.soramitsu.oauth.feature.gatehub.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.uiscreens.compose.Screen
import jp.co.soramitsu.oauth.uiscreens.theme.AuthSdkTheme
import jp.co.soramitsu.ui_core.component.card.ContentCard
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.borderRadius
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

@Composable
fun GatehubOnboardingProgressScreen(
    viewModel: GatehubOnboardingProgressViewModel = hiltViewModel(),
) {
    BackHandler {
        viewModel.onToolbarNavigation()
    }
    Screen(viewModel = viewModel) {
        GatehubOnboardingProgressScreenContent(
            scrollState = it,
        )
    }
}

@Composable
private fun GatehubOnboardingProgressScreenContent(scrollState: ScrollState) {
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
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                modifier = Modifier.size(80.dp),
                painter = painterResource(R.drawable.ic_clock_80),
                contentDescription = null,
                tint = Color.Unspecified,
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x3, bottom = Dimens.x3),
                text = stringResource(R.string.almost_there),
                style = MaterialTheme.customTypography.headline1,
                color = MaterialTheme.customColors.fgPrimary,
                textAlign = TextAlign.Center,
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(R.string.gatehub_onboarding_description),
                style = MaterialTheme.customTypography.paragraphM,
                color = MaterialTheme.customColors.fgPrimary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewGatehubOnboardingProgressScreenContent() {
    AuthSdkTheme {
        GatehubOnboardingProgressScreenContent(
            scrollState = rememberScrollState(),
        )
    }
}
