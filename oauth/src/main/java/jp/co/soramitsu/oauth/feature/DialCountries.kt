package jp.co.soramitsu.oauth.feature

import java.util.Locale

fun String.flagEmoji(): String {
    val locale = Locale("", this)
    val firstLetter = Character.codePointAt(locale.country, 0) - 0x41 + 0x1F1E6
    val secondLetter = Character.codePointAt(locale.country, 1) - 0x41 + 0x1F1E6
    return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
}
