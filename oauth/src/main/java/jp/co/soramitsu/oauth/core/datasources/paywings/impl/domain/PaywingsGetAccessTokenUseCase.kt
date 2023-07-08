package jp.co.soramitsu.oauth.core.datasources.paywings.impl.domain

import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import com.paywings.oauth.android.sdk.service.callback.GetNewAccessTokenCallback
import jp.co.soramitsu.oauth.core.datasources.paywings.api.PayWingsResponse
import jp.co.soramitsu.oauth.core.datasources.paywings.impl.utils.parseToString
import jp.co.soramitsu.oauth.theme.views.Text
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class PaywingsGetAccessTokenUseCase @Inject constructor() {

    private var callback: GetNewAccessTokenCallback? = null

    val getNewAccessTokenCallbackFlow =
        callbackFlow {
            callback = object : GetNewAccessTokenCallback {
                override fun onError(error: OAuthErrorCode, errorMessage: String?) {
                    trySend(
                        PayWingsResponse.Error.OnGetNewAccessToken(
                            errorText = Text.SimpleText(
                                text = errorMessage ?: error.parseToString()
                            )
                        )
                    )
                }

                override fun onNewAccessToken(accessToken: String, accessTokenExpirationTime: Long) {
                    trySend(
                        PayWingsResponse.Result.ReceivedNewAccessToken(
                            accessToken = accessToken,
                            accessTokenExpirationTime = accessTokenExpirationTime
                        )
                    )
                }

                override fun onUserSignInRequired() {
                    trySend(
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