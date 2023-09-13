package jp.co.soramitsu.oauth.base.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import java.util.Arrays

sealed interface Text {

    class SimpleText(val text: String): Text

    class StringRes(val id: Int): Text

    class StringResWithArgs(val id: Int, val payload: Array<Any>): Text

    class StringPluralWithArgs(val id: Int, val amount: Int, val payload: Array<Any>): Text
}

@Composable
fun Text.retrieveString(): String = when(this) {
    is Text.StringRes -> stringResource(id = id)
    is Text.StringResWithArgs -> stringResource(id = id, formatArgs = payload)
    is Text.StringPluralWithArgs -> pluralStringResource(id = id, count = amount, formatArgs = payload)
    is Text.SimpleText -> text
}


