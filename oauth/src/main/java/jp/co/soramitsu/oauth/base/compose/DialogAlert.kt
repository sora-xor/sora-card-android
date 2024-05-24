package jp.co.soramitsu.oauth.base.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import jp.co.soramitsu.androidfoundation.compose.toTitle
import jp.co.soramitsu.oauth.base.state.DialogAlertState
import jp.co.soramitsu.oauth.base.testTagAsId
import jp.co.soramitsu.ui_core.component.button.TextButton
import jp.co.soramitsu.ui_core.component.button.properties.Order
import jp.co.soramitsu.ui_core.component.button.properties.Size
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

@Composable
fun DialogAlert(state: DialogAlertState) {
    val title = state.title?.toTitle()
    val message = state.message?.toTitle()
    AlertDialog(
        title = title?.let {
            { Text(text = title, style = MaterialTheme.customTypography.headline3) }
        },
        text = message?.let {
            { Text(text = message, style = MaterialTheme.customTypography.textM) }
        },
        onDismissRequest = state.onDismiss,
        backgroundColor = MaterialTheme.customColors.bgPage,
        confirmButton = {
            TextButton(
                modifier = Modifier
                    .testTagAsId("DialogConfirmationButton")
                    .padding(vertical = Dimens.x1),
                text = stringResource(id = android.R.string.ok).uppercase(),
                size = Size.ExtraSmall,
                order = Order.SECONDARY,
                onClick = state.onPositive,
            )
        },
    )
}
