package jp.co.soramitsu.oauth.uiscreens.clientsui.soracarddetails

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.testTagAsId
import jp.co.soramitsu.oauth.uiscreens.styledui.FilledLargePrimaryButton
import jp.co.soramitsu.oauth.uiscreens.theme.AuthSdkTheme
import jp.co.soramitsu.ui_core.component.button.BleachedButton
import jp.co.soramitsu.ui_core.component.button.properties.Order
import jp.co.soramitsu.ui_core.component.button.properties.Size
import jp.co.soramitsu.ui_core.component.card.ContentCard
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

data class BasicBannerCardCallback(
    val onButtonClicked: () -> Unit,
    val onCloseCard: () -> Unit,
)

@Composable
fun BasicBannerCardScreen(
    @DrawableRes imageContent: Int,
    title: String,
    description: String,
    button: String,
    buttonEnabled: Boolean = true,
    closeEnabled: Boolean,
    callback: BasicBannerCardCallback,
) {
    ContentCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = if (buttonEnabled) callback.onButtonClicked else null,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
            ) {
                val (card, image) = createRefs()
                CardContent(
                    modifier = Modifier
                        .testTagAsId("StartInviting")
                        .constrainAs(card) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(image.start)
                            width = Dimension.fillToConstraints
                            height = Dimension.wrapContent
                        },
                    title = title,
                    description = description,
                    button = button,
                    onStartClicked = callback.onButtonClicked,
                    buttonEnabled = buttonEnabled,
                )

                Image(
                    modifier = Modifier.constrainAs(image) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                        width = Dimension.value(128.dp)
                        height = Dimension.fillToConstraints
                    },
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.BottomEnd,
                    painter = painterResource(imageContent),
                    contentDescription = null,
                )
            }

            if (closeEnabled) {
                BleachedButton(
                    modifier = Modifier
                        .wrapContentWidth()
                        .align(Alignment.TopEnd)
                        .padding(Dimens.x1)
                        .alpha(0.8f),
                    size = Size.ExtraSmall,
                    order = Order.TERTIARY,
                    shape = CircleShape,
                    onClick = callback.onCloseCard,
                    leftIcon = painterResource(jp.co.soramitsu.ui_core.R.drawable.ic_cross),
                )
            }
        }
    }
}

@Composable
private fun CardContent(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    button: String,
    buttonEnabled: Boolean,
    onStartClicked: () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(
                start = Dimens.x3,
                top = Dimens.x3,
                bottom = Dimens.x2,
            ),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = MaterialTheme.customTypography.headline2,
            color = MaterialTheme.customColors.fgPrimary,
        )

        Text(
            modifier = Modifier.padding(top = Dimens.x1),
            text = description,
            style = MaterialTheme.customTypography.paragraphXS,
            color = MaterialTheme.customColors.fgPrimary,
        )

        FilledLargePrimaryButton(
            modifier = Modifier
                .wrapContentWidth()
                .padding(top = Dimens.x1_5),
            text = TextValue.SimpleText(text = button),
            enabled = buttonEnabled,
            onClick = onStartClicked,
        )
    }
}

@Preview
@Composable
private fun PreviewBasicBannerCard1() {
    AuthSdkTheme {
        BasicBannerCardScreen(
            imageContent = R.drawable.image_friends,
            title = "Some title of banner card, let it be longeeerr",
            description = "Long description of banner card, The quick brown fox jumps over the lazy dog, The quick brown fox jumps over the lazy dog.And I, even I Artaxerxes the king, do make a decree to all the treasurers which are beyond the river, that whatsoever Ezra the priest, the scribe of the law of the God of heaven, shall require of you, it be done speedily",
            button = "Just button title",
            buttonEnabled = true,
            closeEnabled = true,
            callback = BasicBannerCardCallback({}, {}),
        )
    }
}

@Preview
@Composable
private fun PreviewBasicBannerCard12() {
    AuthSdkTheme {
        BasicBannerCardScreen(
            imageContent = R.drawable.image_friends,
            title = "Some title",
            description = "Long description of banner",
            button = "Just button title",
            buttonEnabled = true,
            closeEnabled = false,
            callback = BasicBannerCardCallback({}, {}),
        )
    }
}

@Preview
@Composable
private fun PreviewBasicBannerCard2() {
    AuthSdkTheme {
        BasicBannerCardScreen(
            imageContent = R.drawable.ic_buy_xor_banner_sora,
            title = "Title",
            description = "Description",
            button = "Button",
            buttonEnabled = true,
            closeEnabled = true,
            callback = BasicBannerCardCallback({}, {}),
        )
    }
}
