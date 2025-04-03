package jp.co.soramitsu.oauth.uiscreens.clientsui.soracarddetails

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.testTagAsId
import jp.co.soramitsu.oauth.uiscreens.styledui.LargeBleachedButton
import jp.co.soramitsu.ui_core.component.button.properties.Size
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

@Composable
fun AmountCardIconScreen(
    testTagId: String? = null,
    @DrawableRes res: Int,
    text: String,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
) {
    val reusableButtonModifier = remember(testTagId) {
        Modifier.run {
            return@run if (testTagId == null) {
                this
            } else {
                testTagAsId(testTagId)
            }
        }.fillMaxSize()
    }

    Column(
        modifier = Modifier.wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.size(Size.Large)) {
            LargeBleachedButton(
                modifier = reusableButtonModifier,
                enabled = isEnabled,
                res = res,
                onClick = onClick,
                text = "",
                iconSize = 56.dp,
            )
        }
        Text(
            modifier = Modifier
                .wrapContentSize()
                .padding(top = Dimens.x1),
            text = text,
            style = MaterialTheme.customTypography.textXSBold,
            color = MaterialTheme.customColors.fgSecondary,
        )
    }
}

@Preview
@Composable
fun AmountCardIconPreview() {
    AmountCardIconScreen(
        res = R.drawable.ic_snow_flake,
        text = "Text",
        onClick = {},
    )
}
