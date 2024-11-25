package jp.co.soramitsu.oauth.uiscreens.clientsui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.ui_core.resources.Dimens

@Composable
fun SoraCardImage(modifier: Modifier = Modifier, enabled: Boolean = true) {
    Box(
        modifier = modifier,
    ) {
        Image(
            modifier = Modifier.fillMaxWidth(),
            painter = painterResource(id = R.drawable.sora_card),
            contentDescription = "",
            contentScale = ContentScale.FillWidth,
            alpha = if (enabled) 1.0f else 0.4f,
        )
        Image(
            modifier = Modifier
                .padding(start = Dimens.x2, top = Dimens.x2)
                .wrapContentSize(),
            painter = painterResource(id = R.drawable.ic_sora_on_card),
            contentDescription = "",
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
@Preview(locale = "en")
private fun PreviewSoraCardImage1() {
    SoraCardImage(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        enabled = false,
    )
}

@Composable
@Preview(locale = "ar")
private fun PreviewSoraCardImage2() {
    SoraCardImage(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    )
}

@Composable
@Preview(locale = "he")
private fun PreviewSoraCardImage3() {
    SoraCardImage(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    )
}
