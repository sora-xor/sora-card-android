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

class PaywingsCheckEmailStatusUseCase @Inject constructor(
    private val userSessionRepository: UserSessionRepository
) {

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
        val (accessToken, accessTokenExpirationTime, refreshToken) =
            userSessionRepository.run {
                Triple(getAccessToken(), getAccessTokenExpirationTime(), getRefreshToken())
            }

        if (accessToken.isBlank() || refreshToken.isBlank() ||
            accessTokenExpirationTime <= TimeUnit.MILLISECONDS
                .toSeconds(System.currentTimeMillis())
        ) it.onSignInSuccessful(
            accessToken = accessToken,
            accessTokenExpirationTime = accessTokenExpirationTime,
            refreshToken = refreshToken
        ) else PayWingsOAuthClient.instance.checkEmailVerified(callback = it)
    }

}