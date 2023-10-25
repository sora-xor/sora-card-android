package jp.co.soramitsu.oauth.feature.verify.phone

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jp.co.soramitsu.oauth.base.compose.Screen
import jp.co.soramitsu.oauth.common.model.CountryDial
import jp.co.soramitsu.oauth.feature.flagEmoji
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

@Composable
fun CountryListScreen(
    viewModel: CountryListViewModel = hiltViewModel(),
) {
    Screen(
        viewModel = viewModel
    ) {
        val state = viewModel.state.collectAsStateWithLifecycle().value
        CountriesScreen(
            state = state,
            onSelect = viewModel::onSelect,
        )
    }
}

@Composable
private fun CountriesScreen(
    state: CountryListState,
    onSelect: (Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                vertical = Dimens.x2,
                horizontal = Dimens.x3
            ),
    ) {
        if (state.loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(Dimens.x6)
                    .align(Alignment.Center),
                color = MaterialTheme.customColors.fgPrimary,
            )
        }
        val listState = rememberLazyListState()
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            items(
                count = state.list.size,
            ) { index ->
                val countryDial = state.list[index]
                Row(
                    modifier = Modifier
                        .clickable { onSelect.invoke(index) }
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(vertical = Dimens.x1)
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
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewCountriesScreen() {
    CountriesScreen(
        state = CountryListState(
            loading = true,
            list = listOf(
                CountryDial("GB", "Gre Brit", "+123"),
                CountryDial("VE", "Gre Brit", "+123"),
                CountryDial("IR", "Gre Brit", "+123"),
            )
        ),
        onSelect = {},
    )
}
