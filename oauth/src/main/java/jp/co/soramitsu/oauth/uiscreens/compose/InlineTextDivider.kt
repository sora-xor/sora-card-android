package jp.co.soramitsu.oauth.uiscreens.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextAlign
import jp.co.soramitsu.oauth.uiscreens.theme.tokens.Colors
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customTypography

@Composable
fun InlineTextDivider() {
    // TODO extract to UI module

    var layout: TextLayoutResult? = null
    Text(
        text = "or",
        style = MaterialTheme.customTypography.textM,
        textAlign = TextAlign.Center,
        onTextLayout = {
            layout = it
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.x3)
            .drawBehind {
                val l = layout ?: return@drawBehind

                val heightSplit = size.height / 2
                val thickness = 2f
                drawPath(
                    path = Path().apply {
                        moveTo(Dimens.x7.value, l.getLineBottom(0) - heightSplit)
                        lineTo(l.getLineLeft(0) - Dimens.x2.value, l.getLineBottom(0) - heightSplit)

                        moveTo(
                            l.getLineRight(0) + Dimens.x2.value,
                            l.getLineBottom(0) - heightSplit,
                        )
                        lineTo(size.width - Dimens.x7.value, l.getLineBottom(0) - heightSplit)
                    },
                    Colors.Yellow30,
                    style = Stroke(width = thickness),
                )
            },
    )
}
