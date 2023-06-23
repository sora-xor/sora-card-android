package jp.co.soramitsu.oauth.core.datasources.paywings.impl.domain

import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import com.paywings.oauth.android.sdk.service.callback.GetNewAccessTokenCallback
import jp.co.soramitsu.oauth.core.datasources.paywings.api.PayWingsResponse
import jp.co.soramitsu.oauth.core.datasources.paywings.impl.utils.parseToString
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class PaywingsGetAccessTokenUseCase @Inject constructor() {

    private var callback: GetNewAccessTokenCallback? = null

    val getNewAccessTokenCallbackFlow =
        callbackFlow {
            callback = object : GetNewAccessTokenCallback {
                override fun onError(error: OAuthErrorCode, errorMessage: String?) {
                    trySendBlocking(
                        PayWingsResponse.Error.OnGetNewAccessToken(
                            errorMessage = errorMessage ?: error.parseToString()
                        )
                    )
                }

                override fun onNewAccessToken(accessToken: String, accessTokenExpirationTime: Long) {
                    trySendBlocking(
                        PayWingsResponse.Result.ReceivedNewAccessToken(
                            accessToken = accessToken,
                            accessTokenExpirationTime = accessTokenExpirationTime
                        )
                    )
                }

                override fun onUserSignInRequired() {
                    trySendBlocking(
                        PayWingsResponse.NavigationIncentive.OnUserSignInRequiredScreen()
                    )
                }
            }

            awaitClose { callback = null }
        }

    suspend operator fun invoke(refreshToken: String) = callback?.let {
        PayWingsOAuthClient.instance.getNewAccessToken(
            refreshToken = refreshToken,
            callback = it
        )
    }

}