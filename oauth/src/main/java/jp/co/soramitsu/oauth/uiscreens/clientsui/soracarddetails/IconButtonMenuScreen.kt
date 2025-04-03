package jp.co.soramitsu.oauth.uiscreens.clientsui.soracarddetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import jp.co.soramitsu.androidfoundation.format.ImageValue
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.androidfoundation.format.retrieveString

data class IconButtonMenuState(
    val testTagId: String? = null,
    val image: ImageValue.ResImage,
    val text: TextValue,
    val isEnabled: Boolean,
)

@Composable
fun IconButtonMenuScreen(
    iconButtonMenuStates: List<IconButtonMenuState>,
    onButtonClick: (position: Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        repeat(iconButtonMenuStates.size) {
            AmountCardIconScreen(
                testTagId = iconButtonMenuStates[it].testTagId,
                res = iconButtonMenuStates[it].image.id,
                text = iconButtonMenuStates[it].text.retrieveString(),
                isEnabled = iconButtonMenuStates[it].isEnabled,
                onClick = { onButtonClick.invoke(it) },
            )
        }
    }
}
