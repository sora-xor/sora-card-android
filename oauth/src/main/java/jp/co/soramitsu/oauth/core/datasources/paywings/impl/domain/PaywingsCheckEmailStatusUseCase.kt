package jp.co.soramitsu.oauth.core.datasources.paywings.impl.domain

import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import com.paywings.oauth.android.sdk.service.callback.CheckEmailVerifiedCallback
import jp.co.soramitsu.oauth.core.datasources.paywings.api.PayWingsResponse
import jp.co.soramitsu.oauth.core.datasources.paywings.impl.utils.parseToString
import jp.co.soramitsu.oauth.core.datasources.session.api.UserSessionRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PaywingsCheckEmailStatusUseCase @Inject constructor() {

    private var callback: CheckEmailVerifiedCallback? = null

    val checkEmailVerifiedCallbackFlow =
        callbackFlow<PayWingsResponse> {
            callback = object : CheckEmailVerifiedCallback {
                override fun onEmailNotVerified() {
                    trySendBlocking(
                        PayWingsResponse.Result.ResendDelayedVerificationEmailRepeatedly()
                    )
                }

                override fun onError(error: OAuthErrorCode, errorMessage: String?) {
                    trySendBlocking(
                        PayWingsResponse.Error.OnCheckEmailVerification(
                            errorMessage = errorMessage ?: error.parseToString()
                        )
                    )
                }

                override fun onSignInSuccessful(
                    refreshToken: String,
                    accessToken: String,
                    accessTokenExpirationTime: Long
                ) {
                    trySendBlocking(
                        PayWingsResponse.Result.ReceivedAccessTokens(
                            accessToken = accessToken,
                            accessTokenExpirationTime = accessTokenExpirationTime,
                            refreshToken = refreshToken
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

    suspend operator fun invoke() = callback?.let {
        PayWingsOAuthClient.instance.checkEmailVerified(callback = it)
    }

}