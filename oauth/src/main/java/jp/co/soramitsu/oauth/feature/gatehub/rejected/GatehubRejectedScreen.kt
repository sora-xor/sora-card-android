package jp.co.soramitsu.oauth.feature.gatehub.rejected

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.uiscreens.compose.Screen
import jp.co.soramitsu.oauth.uiscreens.styledui.LargeTonalButton
import jp.co.soramitsu.oauth.uiscreens.theme.AuthSdkTheme
import jp.co.soramitsu.ui_core.component.card.ContentCard
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.borderRadius
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

@Composable
fun GatehubOnboardingRejectedScreen(
    reason: String,
    viewModel: GatehubRejectedViewModel = hiltViewModel(),
) {
    BackHandler {
        viewModel.onToolbarNavigation()
    }
    Screen(viewModel = viewModel) {
        GatehubRejectedScreenContent(
            reason = reason,
            scrollState = it,
            onSupport = viewModel::onSupportClick,
        )
    }
}

@Composable
private fun GatehubRejectedScreenContent(
    reason: String,
    scrollState: ScrollState,
    onSupport: () -> Unit,
) {
    ContentCard(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.customColors.bgSurface)
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
                painter = painterResource(R.drawable.ic_rejected_24),
                contentDescription = null,
                tint = MaterialTheme.customColors.statusError,
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x3, bottom = Dimens.x3),
                text = stringResource(R.string.error_occured),
                style = MaterialTheme.customTypography.headline1,
                color = MaterialTheme.customColors.fgPrimary,
                textAlign = TextAlign.Center,
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(R.string.gatehub_unable_complete),
                style = MaterialTheme.customTypography.paragraphM,
                color = MaterialTheme.customColors.fgPrimary,
                textAlign = TextAlign.Center,
            )
            if (reason.isNotEmpty()) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = reason,
                    style = MaterialTheme.customTypography.paragraphM,
                    color = MaterialTheme.customColors.fgPrimary,
                    textAlign = TextAlign.Center,
                )
            }
            LargeTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x3),
                enabled = true,
                text = TextValue.StringRes(R.string.common_support),
                onClick = onSupport,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewGatehubRejectedScreenContent() {
    AuthSdkTheme {
        GatehubRejectedScreenContent(
            reason = "Nobody known",
            scrollState = rememberScrollState(),
            onSupport = {},
        )
    }
}
