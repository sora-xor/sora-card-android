package jp.co.soramitsu.oauth.core.datasources.paywings.impl.domain

import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import com.paywings.oauth.android.sdk.service.callback.RegisterUserCallback
import jp.co.soramitsu.oauth.core.datasources.paywings.impl.utils.parseToString
import jp.co.soramitsu.oauth.core.datasources.paywings.api.PayWingsResponse
import jp.co.soramitsu.oauth.theme.views.Text
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class PaywingsRegisterUserUseCase @Inject constructor() {

    private var callback: RegisterUserCallback? = null

    val registerUserCallbackFlow =
        callbackFlow<PayWingsResponse> {
            callback = object : RegisterUserCallback {
                override fun onError(error: OAuthErrorCode, errorMessage: String?) {
                    trySend(
                        PayWingsResponse.Error.OnRegisterUser(
                            errorText = Text.SimpleText(
                                text = errorMessage ?: error.parseToString()
                            )
                        )
                    )
                }

                override fun onShowEmailConfirmationScreen(email: String, autoEmailSent: Boolean) {
                    trySend(
                        PayWingsResponse.NavigationIncentive.OnEmailConfirmationRequiredScreen(
                            email = email,
                            autoEmailBeenSent = autoEmailSent
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

    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        email: String
    ) = callback?.let {
        PayWingsOAuthClient.instance.registerUser(
            firstName = firstName,
            lastName = lastName,
            email = email,
            callback = it
        )
    }
}