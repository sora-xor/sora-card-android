package jp.co.soramitsu.oauth.base.extension

fun String.formatForAuth(): String {
    val phoneNumber = this.filter { it.isDigit() }
    return "+$phoneNumber"
}
