package jp.co.soramitsu.oauth.feature.verify

fun String.formatForAuth(): String {
    val phoneNumber = this.filter { it.isDigit() }
    return "+$phoneNumber"
}
