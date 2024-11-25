package jp.co.soramitsu.oauth.uiscreens.clientsui.soracarddetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.testTagAsId
import jp.co.soramitsu.oauth.clients.ClientsFacade
import jp.co.soramitsu.oauth.uiscreens.theme.AuthSdkTheme
import jp.co.soramitsu.ui_core.component.card.ContentCard
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

data class SoraCardIBANCardState(
    val iban: String,
    val closed: Boolean,
)

@Composable
fun SoraCardIBANCardScreen(
    soraCardIBANCardState: SoraCardIBANCardState,
    onShareClick: () -> Unit,
    onCardClick: () -> Unit,
) {
    ContentCard(
        modifier = remember {
            Modifier.testTagAsId("IbanCardClick")
        },
        cornerRadius = Dimens.x4,
        onClick = onCardClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = Dimens.x2),
        ) {
            Row(
                modifier = Modifier
                    .padding(all = Dimens.x1)
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier
                        .wrapContentSize(),
                    text = stringResource(id = R.string.cardhub_iban_title),
                    style = MaterialTheme.customTypography.headline2,
                    color = MaterialTheme.customColors.fgPrimary,
                    textAlign = TextAlign.Center,
                )
                if (soraCardIBANCardState.iban.isNotEmpty() && soraCardIBANCardState.closed.not()) {
                    Icon(
                        modifier = Modifier
                            .testTagAsId("IbanCardShareClick")
                            .clickable(onClick = onShareClick)
                            .wrapContentSize(),
                        painter = painterResource(id = R.drawable.ic_rectangular_arrow_up),
                        contentDescription = null,
                        tint = MaterialTheme.customColors.fgSecondary,
                    )
                }
            }
            if (soraCardIBANCardState.closed) {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(all = Dimens.x1),
                    text = underlineSubstring(
                        stringResource(
                            id = R.string.iban_frozen_description,
                            ClientsFacade.TECH_SUPPORT,
                        ),
                        ClientsFacade.TECH_SUPPORT,
                    ),
                    style = MaterialTheme.customTypography.textS,
                    color = MaterialTheme.customColors.fgSecondary,
                )
            } else if (soraCardIBANCardState.iban.isNotEmpty()) {
                Text(
                    modifier = Modifier
                        .testTagAsId("IbanValueText")
                        .fillMaxWidth()
                        .padding(all = Dimens.x1),
                    text = soraCardIBANCardState.iban,
                    style = MaterialTheme.customTypography.textM,
                    color = MaterialTheme.customColors.fgPrimary,
                )
            } else {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(all = Dimens.x1),
                    text = underlineSubstring(
                        stringResource(
                            id = R.string.iban_pending_description,
                            ClientsFacade.TECH_SUPPORT,
                        ),
                        ClientsFacade.TECH_SUPPORT,
                    ),
                    style = MaterialTheme.customTypography.textS,
                    color = MaterialTheme.customColors.fgSecondary,
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewSoraCardIBANCard01() {
    AuthSdkTheme {
        SoraCardIBANCardScreen(
            soraCardIBANCardState = SoraCardIBANCardState(
                iban = "LT61 3250 0467 7252 5583",
                closed = false,
            ),
            onShareClick = {},
            onCardClick = {},
        )
    }
}

@Preview
@Composable
private fun PreviewSoraCardIBANCard02() {
    AuthSdkTheme {
        SoraCardIBANCardScreen(
            soraCardIBANCardState = SoraCardIBANCardState(
                iban = "",
                closed = false,
            ),
            onShareClick = {},
            onCardClick = {},
        )
    }
}

@Preview
@Composable
private fun PreviewSoraCardIBANCard03() {
    AuthSdkTheme {
        SoraCardIBANCardScreen(
            soraCardIBANCardState = SoraCardIBANCardState(
                iban = "",
                closed = true,
            ),
            onShareClick = {},
            onCardClick = {},
        )
    }
}
