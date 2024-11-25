package jp.co.soramitsu.oauth.feature.verify.phone.uiscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.common.model.countryDialList
import jp.co.soramitsu.oauth.feature.flagEmoji
import jp.co.soramitsu.oauth.feature.verify.phone.CountryListMode
import jp.co.soramitsu.oauth.feature.verify.phone.CountryListState
import jp.co.soramitsu.oauth.feature.verify.phone.CountryListViewModel
import jp.co.soramitsu.oauth.uiscreens.clientsui.UiStyle
import jp.co.soramitsu.oauth.uiscreens.clientsui.localCompositionUiStyle
import jp.co.soramitsu.oauth.uiscreens.compose.Screen
import jp.co.soramitsu.oauth.uiscreens.styledui.FilledLargePrimaryButton
import jp.co.soramitsu.oauth.uiscreens.styledui.fw.pink
import jp.co.soramitsu.oauth.uiscreens.theme.AuthSdkTheme
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

@Composable
fun CountryListScreen(viewModel: CountryListViewModel = hiltViewModel()) {
    Screen(
        viewModel = viewModel,
    ) {
        val state = viewModel.state.collectAsStateWithLifecycle().value
        CountriesScreen(
            state = state,
            onSelect = viewModel::onSelect,
            onDone = viewModel::onDone,
        )
    }
}

@Composable
private fun CountriesScreen(state: CountryListState, onSelect: (Int) -> Unit, onDone: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.customColors.bgSurface)
            .padding(
                vertical = Dimens.x2,
                horizontal = Dimens.x3,
            ),
    ) {
        if (state.loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(top = Dimens.x4)
                    .size(Dimens.x6)
                    .align(Alignment.CenterHorizontally),
                color = MaterialTheme.customColors.fgPrimary,
            )
        } else {
            CountryListContent(
                state = state,
                onSelect = onSelect,
            )
            if (state.countryListMode is CountryListMode.MultiChoice) {
                FilledLargePrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = TextValue.StringRes(id = R.string.common_done),
                    enabled = true,
                    onClick = onDone,
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.CountryListContent(state: CountryListState, onSelect: (Int) -> Unit) {
    val listState = rememberLazyListState()
    val iconTintColor = when (localCompositionUiStyle.current) {
        UiStyle.FW -> pink
        UiStyle.SW -> Color(0xff007aff)
    }
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
    ) {
        items(
            count = state.list.size,
        ) { index ->
            val countryDial = state.list[index]
            Row(
                modifier = Modifier
                    .clickable { onSelect.invoke(index) }
                    .fillMaxWidth()
                    .height(44.dp),
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
                )

                when (state.countryListMode) {
                    is CountryListMode.MultiChoice -> {
                        if (countryDial.code in state.countryListMode.selectedCodes) {
                            Icon(
                                modifier = Modifier
                                    .size(44.dp)
                                    .padding(horizontal = 11.dp),
                                painter = painterResource(R.drawable.ic_checkmark),
                                contentDescription = null,
                                tint = iconTintColor,
                            )
                        }
                    }

                    CountryListMode.SingleChoice -> {
                        Text(
                            modifier = Modifier
                                .wrapContentHeight(),
                            text = countryDial.dialCode,
                            style = MaterialTheme.customTypography.textM,
                            color = MaterialTheme.customColors.fgPrimary,
                        )
                    }
                }
            }
            if (state.countryListMode is CountryListMode.MultiChoice) {
                Divider(color = Color(0x44545456))
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewCountriesScreen() {
    AuthSdkTheme {
        CountriesScreen(
            state = CountryListState(
                loading = false,
                countryListMode = CountryListMode.MultiChoice(
                    selectedCodes = listOf("RU"),
                ),
                list = countryDialList,
            ),
            onSelect = {},
            onDone = {},
        )
    }
}
