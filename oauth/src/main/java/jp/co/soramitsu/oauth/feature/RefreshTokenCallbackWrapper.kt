package jp.co.soramitsu.oauth.feature

import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.service.callback.GetNewAccessTokenCallback

class RefreshTokenCallbackWrapper(
    onError: (error: OAuthErrorCode) -> Unit,
    onNewAccessToken: (accessToken: String, accessTokenExpirationTime: Long) -> Unit,
    onUserSignInRequired: () -> Unit,
) {
    val getNewAccessTokenCallback: GetNewAccessTokenCallback

    init {
        getNewAccessTokenCallback = object : GetNewAccessTokenCallback {
            override fun onError(error: OAuthErrorCode, errorMessage: String?) {
                onError(error)
            }

            override fun onNewAccessToken(accessToken: String, accessTokenExpirationTime: Long) {
                onNewAccessToken(accessToken, accessTokenExpirationTime)
            }

            override fun onUserSignInRequired() {
                onUserSignInRequired()
            }
        }
    }
}
