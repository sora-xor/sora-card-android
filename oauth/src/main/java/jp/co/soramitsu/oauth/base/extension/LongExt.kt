package jp.co.soramitsu.oauth.base.extension

private const val TIME_FORMAT = "RE-SEND IN %02d:%02d"
private const val SECOND_IN_MILLS = 1000L
private const val SECOND_IN_MINUTES_IN_MILLS = SECOND_IN_MILLS * 60

fun Long.format(): String {
    val minutes = this.toInt() / SECOND_IN_MINUTES_IN_MILLS
    val seconds = this.toInt() % SECOND_IN_MINUTES_IN_MILLS / SECOND_IN_MILLS

    return TIME_FORMAT.format(minutes, seconds)
}
