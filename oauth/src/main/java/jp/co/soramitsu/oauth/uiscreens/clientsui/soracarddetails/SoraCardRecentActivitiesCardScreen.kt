package jp.co.soramitsu.oauth.uiscreens.clientsui.soracarddetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.androidfoundation.format.retrieveString
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.uiscreens.theme.AuthSdkTheme
import jp.co.soramitsu.ui_core.component.card.ContentCard
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

data class SoraCardRecentActivity(
    val t: String,
) {
    /* implementation will be added in further releases */
}

data class SoraCardRecentActivitiesCardState(
    val data: List<SoraCardRecentActivity>,
) {

    val headlineText: TextValue = TextValue.StringRes(
        id = R.string.recent_activity,
    )

    val visibleListTileCount: Int = data.size

    val recentActivitiesState: List<ListTileState> = data.map {
        ListTileState(
            variant = ListTileVariant.TITLE_SUBTITLE_BODY,
            flag = ListTileFlag.NORMAL,
            title = TextValue.SimpleText(text = it.t),
            subtitle = TextValue.SimpleText(text = it.t),
            body = TextValue.SimpleText(text = it.t),
        )
    }

    val showMoreText: TextValue = TextValue.StringRes(
        id = R.string.show_more,
    )
}

@Composable
fun SoraCardRecentActivitiesCardScreen(
    soraCardRecentActivitiesCardState: SoraCardRecentActivitiesCardState,
    onListTileClick: (position: Int) -> Unit,
    onShowMoreClick: () -> Unit,
) {
    ContentCard(
        cornerRadius = Dimens.x4,
        onClick = onShowMoreClick,
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
                text = soraCardRecentActivitiesCardState.headlineText.retrieveString(),
                style = MaterialTheme.customTypography.headline2,
                color = MaterialTheme.customColors.fgPrimary,
            )
            repeat(
                soraCardRecentActivitiesCardState.visibleListTileCount,
            ) {
                ListTileViewScreen(
                    listTileState = soraCardRecentActivitiesCardState.recentActivitiesState[it],
                    onItemClick = remember { { onListTileClick.invoke(it) } },
                )
            }
            Text(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(top = Dimens.x1)
                    .clickable { onShowMoreClick.invoke() }
                    .padding(all = Dimens.x1),
                text = soraCardRecentActivitiesCardState.showMoreText.retrieveString(),
                style = MaterialTheme.customTypography.textSBold,
                color = MaterialTheme.customColors.accentPrimary,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewRecentActivitiesCard() {
    AuthSdkTheme {
        SoraCardRecentActivitiesCardScreen(
            soraCardRecentActivitiesCardState = SoraCardRecentActivitiesCardState(
                data = listOf(
                    SoraCardRecentActivity(
                        t = "Something",
                    ),
                ),
            ),
            onListTileClick = { _ -> },
            onShowMoreClick = {},
        )
    }
}
