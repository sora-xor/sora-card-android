package jp.co.soramitsu.oauth.core.datasources.paywings.impl.domain

import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import com.paywings.oauth.android.sdk.service.callback.CheckEmailVerifiedCallback
import jp.co.soramitsu.oauth.core.datasources.paywings.api.PayWingsResponse
import jp.co.soramitsu.oauth.core.datasources.paywings.impl.utils.parseToString
import jp.co.soramitsu.oauth.theme.views.Text
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class PaywingsCheckEmailStatusUseCase @Inject constructor() {

    private var callback: CheckEmailVerifiedCallback? = null

    val checkEmailVerifiedCallbackFlow =
        callbackFlow<PayWingsResponse> {
            callback = object : CheckEmailVerifiedCallback {
                override fun onEmailNotVerified() {
                    trySend(
                        PayWingsResponse.Result.ResendDelayedVerificationEmailRepeatedly()
                    )
                }

                override fun onError(error: OAuthErrorCode, errorMessage: String?) {
                    trySend(
                        PayWingsResponse.Error.OnCheckEmailVerification(
                            errorText = Text.SimpleText(
                                text = errorMessage ?: error.parseToString()
                            )
                        )
                    )
                }

                override fun onSignInSuccessful(
                    refreshToken: String,
                    accessToken: String,
                    accessTokenExpirationTime: Long
                ) {
                    trySend(
                        PayWingsResponse.Result.ReceivedAccessTokens(
                            accessToken = accessToken,
                            accessTokenExpirationTime = accessTokenExpirationTime,
                            refreshToken = refreshToken
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

    suspend operator fun invoke() = callback?.let {
        PayWingsOAuthClient.instance.checkEmailVerified(callback = it)
    }
}