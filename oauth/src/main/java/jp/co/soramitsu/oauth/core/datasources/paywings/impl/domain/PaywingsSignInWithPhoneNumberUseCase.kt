package jp.co.soramitsu.oauth.core.datasources.paywings.impl.domain

import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import com.paywings.oauth.android.sdk.service.callback.SignInWithPhoneNumberVerifyOtpCallback
import jp.co.soramitsu.oauth.core.datasources.paywings.api.PayWingsResponse
import jp.co.soramitsu.oauth.core.datasources.paywings.impl.utils.parseToString
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class PaywingsSignInWithPhoneNumberUseCase @Inject constructor() {

    private var callback: SignInWithPhoneNumberVerifyOtpCallback? = null

    val signInWithPhoneNumberVerifyOtpCallbackFlow =
        callbackFlow {
            callback = object : SignInWithPhoneNumberVerifyOtpCallback {
                override fun onError(error: OAuthErrorCode, errorMessage: String?) {
                    trySendBlocking(
                        PayWingsResponse.Error.OnSignInWithPhoneNumberVerifyOtp(
                            errorMessage = errorMessage ?: error.parseToString()
                        )
                    )
                }

                override fun onShowEmailConfirmationScreen(email: String, autoEmailSent: Boolean) {
                    trySendBlocking(
                        PayWingsResponse.NavigationIncentive.OnEmailConfirmationRequiredScreen(
                            email = email,
                            autoEmailBeenSent = autoEmailSent
                        )
                    )
                }

                override fun onShowRegistrationScreen() {
                    trySendBlocking(
                        PayWingsResponse.NavigationIncentive.OnRegistrationRequiredScreen()
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
                        PayWingsResponse.Result.ResendDelayedOtpRepeatedly()
                    )
                }

                override fun onVerificationFailed() {
                    trySendBlocking(
                        PayWingsResponse.Error.OnVerificationByOtpFailed()
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