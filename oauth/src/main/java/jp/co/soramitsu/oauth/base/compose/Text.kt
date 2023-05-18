package jp.co.soramitsu.oauth.base.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import java.util.Arrays

sealed interface Text {

    data class SimpleText(val text: String): Text

    data class StringRes(val id: Int): Text

    data class StringResWithArgs(val id: Int, val payload: Array<Any>): Text {

        override fun equals(other: Any?): Boolean {
            if (other !is StringResWithArgs)
                return false

            return payload.contentEquals(other.payload)
        }

        override fun hashCode(): Int {
            return 137 * id.hashCode() + payload.contentHashCode()
        }

    }
}

@Composable
fun Text.retrieveString(): String = when(this) {
    is Text.StringRes -> stringResource(id = id)
    is Text.StringResWithArgs -> stringResource(id = id, formatArgs = payload)
    is Text.SimpleText -> text
}


