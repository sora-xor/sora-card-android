package jp.co.soramitsu.oauth.feature.gatehub.step2crossbordertx

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.common.model.CountryDial
import jp.co.soramitsu.oauth.common.model.countryDialList
import jp.co.soramitsu.oauth.feature.flagEmoji
import jp.co.soramitsu.oauth.uiscreens.clientsui.UiStyle
import jp.co.soramitsu.oauth.uiscreens.clientsui.localCompositionUiStyle
import jp.co.soramitsu.oauth.uiscreens.compose.Screen
import jp.co.soramitsu.oauth.uiscreens.styledui.FilledLargePrimaryButton
import jp.co.soramitsu.oauth.uiscreens.styledui.fw.white
import jp.co.soramitsu.oauth.uiscreens.theme.AuthSdkTheme
import jp.co.soramitsu.ui_core.component.card.ContentCard
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.borderRadius
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

internal data class CrossBorderTxState(
    val countriesFrom: Boolean,
    val countries: List<CountryDial>,
)

@Composable
fun GatehubOnboardingCrossBorderTxScreen(
    countries: List<String>?,
    viewModel: GatehubCrossBorderTxViewModel = hiltViewModel(),
) {
    BackHandler {
        viewModel.onToolbarNavigation()
    }
    LaunchedEffect(countries) { viewModel.setCountries(countries) }
    Screen(viewModel = viewModel) {
        GatehubCrossBorderTxScreenInternal(
            scrollState = it,
            state = viewModel.state.collectAsStateWithLifecycle().value,
            onDone = viewModel::onDone,
            onAddCountry = viewModel::onAddCountry,
            onRemoveCountry = viewModel::onRemoveCountry,
        )
    }
}

@Composable
private fun GatehubCrossBorderTxScreenInternal(
    modifier: Modifier = Modifier,
    scrollState: ScrollState,
    state: CrossBorderTxState,
    onDone: () -> Unit,
    onAddCountry: () -> Unit,
    onRemoveCountry: (String) -> Unit,
) {
    val title = remember(key1 = state.countriesFrom) {
        if (state.countriesFrom) R.string.gatehub_where_transfer_from else R.string.gatehub_where_transfer_to
    }
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
                .wrapContentHeight(),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(title),
                style = MaterialTheme.customTypography.paragraphM,
                color = MaterialTheme.customColors.fgPrimary,
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x2, bottom = Dimens.x1),
                text = stringResource(R.string.select_many_countries),
                style = MaterialTheme.customTypography.paragraphM,
                color = MaterialTheme.customColors.fgSecondary,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .verticalScroll(scrollState),
            ) {
                state.countries.forEach { countryDial ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(countryDial.code.flagEmoji())
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = Dimens.x1),
                            text = countryDial.name,
                            style = MaterialTheme.customTypography.textM,
                            color = MaterialTheme.customColors.fgPrimary,
                            maxLines = 1,
                        )
                        IconButton(
                            onClick = { onRemoveCountry.invoke(countryDial.code) },
                            modifier = Modifier.size(56.dp),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_cross),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.customColors.fgSecondary,
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable(onClick = onAddCountry),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val iconTint = when (localCompositionUiStyle.current) {
                    UiStyle.SW -> Color(0xff0a0a0a)
                    UiStyle.FW -> white
                }
                Icon(
                    painter = painterResource(jp.co.soramitsu.ui_core.R.drawable.ic_plus),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = iconTint,
                )
                Text(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(horizontal = Dimens.x1),
                    text = stringResource(R.string.add_country),
                    style = MaterialTheme.customTypography.textM,
                    color = MaterialTheme.customColors.fgPrimary,
                    maxLines = 1,
                )
            }

            FilledLargePrimaryButton(
                enabled = state.countries.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x2),
                text = TextValue.StringRes(id = R.string.common_done),
                onClick = onDone,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun GatehubCrossBorderTxScreenInternalPreview() {
    AuthSdkTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
        ) {
            GatehubCrossBorderTxScreenInternal(
                modifier = Modifier.wrapContentSize(),
                scrollState = rememberScrollState(),
                state = CrossBorderTxState(
                    countriesFrom = true,
                    countries = countryDialList,
                ),
                onDone = {},
                onAddCountry = {},
                onRemoveCountry = {},
            )
        }
    }
}
