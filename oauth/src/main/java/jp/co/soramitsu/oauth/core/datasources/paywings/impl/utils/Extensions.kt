package jp.co.soramitsu.oauth.core.datasources.paywings.impl.utils

import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode

fun OAuthErrorCode.parseToString() = when (this) {
    OAuthErrorCode.NO_INTERNET -> "Check your internet connection"
    OAuthErrorCode.USER_IS_SUSPENDED -> "Phone number is suspended"
    else -> ""
}