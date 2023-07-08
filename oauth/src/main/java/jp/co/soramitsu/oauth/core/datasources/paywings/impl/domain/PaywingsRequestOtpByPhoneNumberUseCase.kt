package jp.co.soramitsu.oauth.core.datasources.paywings.impl.domain

import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import com.paywings.oauth.android.sdk.service.callback.SignInWithPhoneNumberRequestOtpCallback
import jp.co.soramitsu.oauth.core.datasources.paywings.impl.utils.parseToString
import jp.co.soramitsu.oauth.core.datasources.paywings.api.PayWingsResponse
import jp.co.soramitsu.oauth.theme.views.Text
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class PaywingsRequestOtpByPhoneNumberUseCase @Inject constructor() {

    private var callback: SignInWithPhoneNumberRequestOtpCallback? = null

    val requestOtpByPhoneNumberCallbackFlow =
        callbackFlow<PayWingsResponse> {
            callback = object : SignInWithPhoneNumberRequestOtpCallback {
                override fun onError(error: OAuthErrorCode, errorMessage: String?) {
                    trySend(
                        PayWingsResponse.Error.OnSignWithPhoneNumberRequestOtp(
                            errorText = Text.SimpleText(
                                text = errorMessage ?: error.parseToString()
                            )
                        )
                    )
                }

                override fun onShowOtpInputScreen(otpLength: Int) {
                    trySend(
                        PayWingsResponse.NavigationIncentive.OnVerificationOtpBeenSent(
                            otpLength = otpLength
                        )
                    )
                }
            }

            awaitClose { callback = null }
        }

    suspend operator fun invoke(
        phoneNumber: String
    ) = callback?.let {
        PayWingsOAuthClient.instance.signInWithPhoneNumberRequestOtp(
            phoneNumber = phoneNumber,
            callback = it
        )
    }
}