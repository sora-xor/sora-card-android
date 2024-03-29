package jp.co.soramitsu.oauth.base.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import jp.co.soramitsu.ui_core.component.button.FilledButton
import jp.co.soramitsu.ui_core.component.button.TextButton
import jp.co.soramitsu.ui_core.component.button.properties.Order
import jp.co.soramitsu.ui_core.component.button.properties.Size
import jp.co.soramitsu.ui_core.component.card.ContentCard
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

@Composable
fun SelectableDialog(
    dialogTitle: String,
    dialogDescription: String,
    selectableChoices: List<String>,
    cancelText: String,
    onChoiceSelectedClickListener: (position: Int) -> Unit,
    onCancelClickListener: () -> Unit,
) {
    // TODO extract to UI module
    Box(
        contentAlignment = Alignment.Center
    ) {
        Dialog(
            onDismissRequest = { /*DO NOTHING*/ }
        ) {
            ContentCard(
                cornerRadius = Dimens.x3,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(Dimens.x3),
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = dialogTitle,
                        style = MaterialTheme.customTypography.textLBold,
                        color = MaterialTheme.customColors.fgPrimary,
                        textAlign = TextAlign.Left
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Dimens.x1_4),
                        text = dialogDescription,
                        style = MaterialTheme.customTypography.textM,
                        color = MaterialTheme.customColors.fgPrimary,
                        textAlign = TextAlign.Left
                    )

                    Spacer(modifier = Modifier.height(Dimens.x1))

                    repeat(selectableChoices.size) { index ->
                        TextButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            text = selectableChoices[index],
                            size = Size.Small,
                            order = Order.PRIMARY,
                            onClick = { onChoiceSelectedClickListener.invoke(index) }
                        )

                        if (index < selectableChoices.lastIndex)
                            Divider()
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Dimens.x1),
                        horizontalArrangement = Arrangement
                            .aligned(Alignment.End),
                    ) {
                        FilledButton(
                            text = "Cancel",
                            order = Order.PRIMARY,
                            size = Size.Small,
                            onClick = onCancelClickListener::invoke,
                        )
                    }
                }
            }
        }
    }
}