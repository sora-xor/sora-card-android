package jp.co.soramitsu.oauth.common.model

import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode

internal sealed interface AccessTokenResponse {
    object SignInRequired : AccessTokenResponse
    class AuthError(val code: OAuthErrorCode) : AccessTokenResponse
    class Token(val token: String, val expirationTime: Long) : AccessTokenResponse
}
