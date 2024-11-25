package jp.co.soramitsu.oauth.uiscreens.clientsui.soracarddetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import jp.co.soramitsu.androidfoundation.format.ImageValue
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.uiscreens.theme.AuthSdkTheme
import jp.co.soramitsu.ui_core.component.card.ContentCard
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

enum class SoraCardSettingsOption {
    MANAGE_SORA_CARD,
    SUPPORT_CHAT,
    LOG_OUT,
}

data class SoraCardSettingsCardState(
    val soraCardSettingsOptions: List<SoraCardSettingsOption>,
    val phone: String,
) {

    val settings: List<ListTileState> = soraCardSettingsOptions.map {
        when (it) {
            SoraCardSettingsOption.LOG_OUT ->
                ListTileState(
                    testTagId = it.toString(),
                    variant = ListTileVariant.TITLE_NAVIGATION_HINT,
                    flag = ListTileFlag.WARNING,
                    title = TextValue.StringRes(id = R.string.card_hub_settings_logout_title),
                    icon = ImageValue.ResImage(
                        id = jp.co.soramitsu.ui_core.R.drawable.ic_arrow_right,
                    ),
                )

            SoraCardSettingsOption.SUPPORT_CHAT ->
                ListTileState(
                    testTagId = it.toString(),
                    variant = ListTileVariant.TITLE_NAVIGATION_HINT,
                    flag = ListTileFlag.NORMAL,
                    title = TextValue.StringRes(id = R.string.support_chat),
                    icon = ImageValue.ResImage(
                        id = jp.co.soramitsu.ui_core.R.drawable.ic_arrow_right,
                    ),
                )

            SoraCardSettingsOption.MANAGE_SORA_CARD -> {
                ListTileState(
                    testTagId = it.toString(),
                    variant = ListTileVariant.TITLE_NAVIGATION_HINT,
                    flag = ListTileFlag.NORMAL,
                    title = TextValue.StringRes(
                        id = R.string.card_hub_manage_card,
                    ),
                    icon = ImageValue.ResImage(
                        id = jp.co.soramitsu.ui_core.R.drawable.ic_arrow_right,
                    ),
                    subtitle = TextValue.SimpleText(text = phone),
                )
            }
        }
    }
}

@Composable
fun SoraCardSettingsCardScreen(
    state: SoraCardSettingsCardState,
    onItemClick: (position: Int) -> Unit,
) {
    ContentCard(
        cornerRadius = Dimens.x4,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = Dimens.x2),
        ) {
            Text(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(horizontal = Dimens.x1)
                    .padding(top = Dimens.x1, bottom = Dimens.x2),
                text = stringResource(id = R.string.card_hub_settings_title),
                style = MaterialTheme.customTypography.headline2,
                color = MaterialTheme.customColors.fgPrimary,
            )
            state.settings.forEachIndexed { i, s ->
                ListTileViewScreen(
                    listTileState = s,
                    onItemClick = { onItemClick.invoke(i) },
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewSoraCardSettingsCard() {
    AuthSdkTheme {
        SoraCardSettingsCardScreen(
            state = SoraCardSettingsCardState(
                soraCardSettingsOptions = SoraCardSettingsOption.entries,
                phone = "109328",
            ),
            onItemClick = { _ -> },
        )
    }
}
