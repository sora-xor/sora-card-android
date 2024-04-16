package jp.co.soramitsu.oauth.feature

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

@Composable
fun YourPhoneNumberText(phone: String, topPadding: Dp = Dimens.x1) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding),
        text = "%s: +%s".format(stringResource(R.string.your_phone_number), phone),
        style = MaterialTheme.customTypography.headline3,
        color = MaterialTheme.customColors.fgPrimary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}
