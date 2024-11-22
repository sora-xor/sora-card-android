package jp.co.soramitsu.oauth.uiscreens.clientsui.soracarddetails

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration

fun underlineSubstring(main: String, sub: String): AnnotatedString = buildAnnotatedString {
    val pos = main.indexOf(sub)
    this.append(main)
    if (pos != -1) {
        addStyle(
            style = SpanStyle(
                textDecoration = TextDecoration.Underline,
            ),
            start = pos,
            end = pos + sub.length,
        )
    }
}
