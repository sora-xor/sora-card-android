package jp.co.soramitsu.oauth.core.datasources.paywings.impl.domain

import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import com.paywings.oauth.android.sdk.service.callback.SendNewVerificationEmailCallback
import jp.co.soramitsu.oauth.core.datasources.paywings.api.PayWingsResponse
import jp.co.soramitsu.oauth.core.datasources.paywings.impl.utils.parseToString
import jp.co.soramitsu.oauth.theme.views.Text
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class PaywingsSendVerificationEmailUseCase @Inject constructor() {

    private var callback: SendNewVerificationEmailCallback? = null

    val sendNewVerificationEmailCallbackFlow =
        callbackFlow {
            callback = object : SendNewVerificationEmailCallback {
                override fun onError(error: OAuthErrorCode, errorMessage: String?) {
                    trySend(
                        PayWingsResponse.Error.OnSendNewVerificationEmail(
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

                override fun onUserSignInRequired() {
                    trySend(
                        PayWingsResponse.NavigationIncentive.OnUserSignInRequiredScreen()
                    )
                }
            }

            awaitClose { callback = null }
        }

    suspend operator fun invoke() = callback?.let {
        PayWingsOAuthClient.instance.sendNewVerificationEmail(
            callback = it
        )
    }
}