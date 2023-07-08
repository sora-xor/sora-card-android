package jp.co.soramitsu.oauth.core.datasources.paywings.impl.domain

import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import com.paywings.oauth.android.sdk.service.callback.SignInWithPhoneNumberVerifyOtpCallback
import jp.co.soramitsu.oauth.core.datasources.paywings.api.PayWingsResponse
import jp.co.soramitsu.oauth.core.datasources.paywings.impl.utils.parseToString
import jp.co.soramitsu.oauth.theme.views.Text
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class PaywingsSignInWithPhoneNumberUseCase @Inject constructor() {

    private var callback: SignInWithPhoneNumberVerifyOtpCallback? = null

    val signInWithPhoneNumberVerifyOtpCallbackFlow =
        callbackFlow {
            callback = object : SignInWithPhoneNumberVerifyOtpCallback {
                override fun onError(error: OAuthErrorCode, errorMessage: String?) {
                    trySend(
                        PayWingsResponse.Error.OnSignInWithPhoneNumberVerifyOtp(
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

                override fun onShowRegistrationScreen() {
                    trySend(
                        PayWingsResponse.NavigationIncentive.OnRegistrationRequiredScreen()
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
                        PayWingsResponse.Result.ResendDelayedOtpRepeatedly()
                    )
                }

                override fun onVerificationFailed() {
                    trySend(
                        PayWingsResponse.Error.OnVerificationByOtpFailed
                    )
                }
            }

            awaitClose { callback = null }
        }

    suspend operator fun invoke(otp: String) = callback?.let {
        PayWingsOAuthClient.instance.signInWithPhoneNumberVerifyOtp(
            otp = otp,
            callback = it
        )
    }
}