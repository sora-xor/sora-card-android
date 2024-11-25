package jp.co.soramitsu.oauth.uiscreens.clientsui.soracarddetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import jp.co.soramitsu.androidfoundation.format.ImageValue
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.androidfoundation.format.retrievePainter
import jp.co.soramitsu.androidfoundation.format.retrieveString
import jp.co.soramitsu.oauth.base.testTagAsId
import jp.co.soramitsu.oauth.uiscreens.theme.AuthSdkTheme
import jp.co.soramitsu.ui_core.R
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

enum class ListTileVariant {
    TITLE_SUBTITLE_BODY,
    TITLE_NAVIGATION_HINT,
}

enum class ListTileFlag {
    NORMAL,
    WARNING,
}

data class ListTileState(
    val testTagId: String? = null,
    val variant: ListTileVariant,
    val flag: ListTileFlag,
    val title: TextValue,
    val subtitle: TextValue? = null,
    val clickEnabled: Boolean = true,
    private val body: TextValue? = null,
    private val icon: ImageValue? = null,
) {

    val isBodyVisible = variant === ListTileVariant.TITLE_SUBTITLE_BODY

    val bodyText: TextValue?
        get() = if (variant === ListTileVariant.TITLE_SUBTITLE_BODY) {
            body
        } else {
            null
        }

    val navigationIcon: ImageValue?
        get() = if (variant === ListTileVariant.TITLE_NAVIGATION_HINT) {
            icon
        } else {
            null
        }
}

@Composable
fun ListTileViewScreen(listTileState: ListTileState, onItemClick: () -> Unit) {
    val colorInUse = if (listTileState.variant === ListTileVariant.TITLE_NAVIGATION_HINT && listTileState.flag === ListTileFlag.WARNING) {
        MaterialTheme.customColors.statusError
    } else {
        MaterialTheme.customColors.fgPrimary
    }

    Row(
        modifier = Modifier
            .run {
                if (listTileState.testTagId == null) {
                    this
                } else {
                    testTagAsId(
                        listTileState.testTagId,
                    )
                }
            }
            .clickable(onClick = onItemClick, enabled = listTileState.clickEnabled)
            .fillMaxWidth()
            .background(MaterialTheme.customColors.bgSurface)
            .padding(
                vertical = Dimens.x1,
                horizontal = Dimens.x1,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterVertically),
        ) {
            Text(
                text = listTileState.title.retrieveString(),
                style = MaterialTheme.customTypography.textM,
                color = colorInUse,
            )
            if (listTileState.subtitle != null) {
                Text(
                    text = listTileState.subtitle.retrieveString(),
                    style = MaterialTheme.customTypography.textXSBold,
                    color = MaterialTheme.customColors.fgSecondary,
                )
            }
        }
        Column {
            if (listTileState.isBodyVisible) {
                Text(
                    text = listTileState.bodyText?.retrieveString().orEmpty(),
                    style = MaterialTheme.customTypography.textM,
                    color = colorInUse,
                )
            }
            if (listTileState.variant == ListTileVariant.TITLE_NAVIGATION_HINT) {
                Icon(
                    painter = listTileState.navigationIcon!!.retrievePainter(),
                    contentDescription = "",
                    tint = colorInUse,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewListTileTitleNavigationHint() {
    AuthSdkTheme {
        ListTileViewScreen(
            listTileState = ListTileState(
                variant = ListTileVariant.TITLE_NAVIGATION_HINT,
                flag = ListTileFlag.WARNING,
                title = TextValue.SimpleText(text = "Title"),
                icon = ImageValue.ResImage(id = R.drawable.ic_arrow_right),
            ),
            onItemClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewListTileTitleSubtitleBody() {
    AuthSdkTheme {
        ListTileViewScreen(
            listTileState = ListTileState(
                variant = ListTileVariant.TITLE_SUBTITLE_BODY,
                flag = ListTileFlag.NORMAL,
                title = TextValue.SimpleText(text = "Title"),
                subtitle = TextValue.SimpleText(text = "Subtitle"),
                body = TextValue.SimpleText(text = "Body"),
            ),
            onItemClick = {},
        )
    }
}
