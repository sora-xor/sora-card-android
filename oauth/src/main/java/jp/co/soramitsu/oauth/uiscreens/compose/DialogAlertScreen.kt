package jp.co.soramitsu.oauth.uiscreens.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.androidfoundation.format.retrieveString
import jp.co.soramitsu.oauth.base.testTagAsId
import jp.co.soramitsu.oauth.base.uiscreens.DialogAlertState
import jp.co.soramitsu.oauth.uiscreens.styledui.TextLargePrimaryButton
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

@Composable
fun DialogAlertScreen(state: DialogAlertState) {
    val title = state.title?.retrieveString()
    val message = state.message?.retrieveString()
    AlertDialog(
        title = title?.let {
            {
                Text(
                    text = title,
                    style = MaterialTheme.customTypography.headline3,
                    color = MaterialTheme.customColors.fgPrimary,
                )
            }
        },
        text = message?.let {
            {
                Text(
                    text = message,
                    style = MaterialTheme.customTypography.textM,
                    color = MaterialTheme.customColors.fgPrimary,
                )
            }
        },
        onDismissRequest = state.onDismiss,
        backgroundColor = MaterialTheme.customColors.bgSurface,
        confirmButton = {
            TextLargePrimaryButton(
                modifier = Modifier
                    .testTagAsId("DialogConfirmationButton")
                    .padding(vertical = Dimens.x1),
                text = TextValue.StringRes(id = android.R.string.ok),
                onClick = state.onPositive,
                enabled = true,
            )
        },
    )
}
