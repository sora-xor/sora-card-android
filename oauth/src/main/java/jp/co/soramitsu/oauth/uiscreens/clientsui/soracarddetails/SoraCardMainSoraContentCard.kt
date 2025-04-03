package jp.co.soramitsu.oauth.uiscreens.clientsui.soracarddetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.co.soramitsu.androidfoundation.format.ImageValue
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.uiscreens.clientsui.SoraCardImage
import jp.co.soramitsu.oauth.uiscreens.styledui.BleachedButtonOnSoraCard
import jp.co.soramitsu.oauth.uiscreens.styledui.IconTonalButton
import jp.co.soramitsu.oauth.uiscreens.styledui.LargeTonalButton
import jp.co.soramitsu.oauth.uiscreens.theme.AuthSdkTheme
import jp.co.soramitsu.ui_core.component.card.ContentCard
import jp.co.soramitsu.ui_core.resources.Dimens

enum class SoraCardMenuAction {
    TOP_UP,
    TRANSFER,
    EXCHANGE,
    FREEZE,
}

data class SoraCardMainSoraContentCardState(
    val balance: String?,
    val phone: String?,
    val actionsEnabled: Boolean = false,
    val soraCardMenuActions: List<SoraCardMenuAction>,
    val canStartGatehubFlow: Boolean,
) {

    val menuState: List<IconButtonMenuState>
        get() = soraCardMenuActions.map {
            when (it) {
                SoraCardMenuAction.TOP_UP ->
                    IconButtonMenuState(
                        testTagId = it.toString(),
                        image = ImageValue.ResImage(id = R.drawable.ic_new_arrow_down_24),
                        text = TextValue.StringRes(id = R.string.cardhub_top_up),
                        isEnabled = actionsEnabled,
                    )

                SoraCardMenuAction.TRANSFER ->
                    IconButtonMenuState(
                        testTagId = it.toString(),
                        image = ImageValue.ResImage(id = R.drawable.ic_new_arrow_up_24),
                        text = TextValue.StringRes(id = R.string.cardhub_transfer),
                        isEnabled = actionsEnabled,
                    )

                SoraCardMenuAction.EXCHANGE ->
                    IconButtonMenuState(
                        testTagId = it.toString(),
                        image = ImageValue.ResImage(id = R.drawable.ic_refresh_24),
                        text = TextValue.StringRes(id = R.string.cardhub_exchange),
                        isEnabled = actionsEnabled,
                    )

                SoraCardMenuAction.FREEZE ->
                    IconButtonMenuState(
                        testTagId = it.toString(),
                        image = ImageValue.ResImage(id = R.drawable.ic_snow_flake),
                        text = TextValue.StringRes(id = R.string.cardhub_freeze),
                        isEnabled = actionsEnabled,
                    )
            }
        }
}

@Composable
fun SoraCardMainSoraContentCardScreen(
    soraCardMainSoraContentCardState: SoraCardMainSoraContentCardState,
    onExchangeXor: () -> Unit,
    onOptionsClick: () -> Unit,
) {
    ContentCard(
        cornerRadius = Dimens.x4,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.x2),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.x2),
        ) {
            Box(
                modifier = Modifier.wrapContentSize(),
            ) {
                SoraCardImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                )

                BleachedButtonOnSoraCard(
                    modifier = Modifier
                        .padding(end = Dimens.x1, bottom = Dimens.x1)
                        .align(Alignment.BottomEnd),
                    text = soraCardMainSoraContentCardState.balance ?: "--",
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
            ) {
                LargeTonalButton(
                    modifier = Modifier
                        .padding(horizontal = Dimens.x1)
                        .weight(1f),
                    enabled = soraCardMainSoraContentCardState.canStartGatehubFlow,
                    onClick = onExchangeXor,
                    text = TextValue.StringRes(id = R.string.exchange_xor),
                )

                IconTonalButton(
                    modifier = Modifier,
                    res = R.drawable.ic_options,
                    enabled = true,
                    onClick = onOptionsClick,
                    iconSize = 20.dp,
                )
            }
        }
    }
}

@Preview(locale = "en")
@Composable
private fun PreviewMainSoraContentCard() {
    SoraCardMainSoraContentCardScreen(
        soraCardMainSoraContentCardState = SoraCardMainSoraContentCardState(
            balance = "3644.50",
            phone = "",
            soraCardMenuActions = SoraCardMenuAction.entries,
            canStartGatehubFlow = true,
        ),
        onExchangeXor = {},
        onOptionsClick = {},
    )
}

@Preview(locale = "ar")
@Composable
private fun PreviewMainSoraContentCard2() {
    SoraCardMainSoraContentCardScreen(
        soraCardMainSoraContentCardState = SoraCardMainSoraContentCardState(
            balance = "3644.50",
            phone = "",
            soraCardMenuActions = SoraCardMenuAction.entries,
            canStartGatehubFlow = true,
        ),
        onExchangeXor = {},
        onOptionsClick = {},
    )
}

@Preview(locale = "en")
@Composable
private fun PreviewMainSoraContentCard3() {
    AuthSdkTheme {
        SoraCardMainSoraContentCardScreen(
            soraCardMainSoraContentCardState = SoraCardMainSoraContentCardState(
                balance = null,
                phone = "",
                soraCardMenuActions = SoraCardMenuAction.entries,
                canStartGatehubFlow = true,
            ),
            onExchangeXor = {},
            onOptionsClick = {},
        )
    }
}
