package jp.co.soramitsu.oauth.core.datasources.paywings.impl.domain

import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import com.paywings.oauth.android.sdk.service.callback.ChangeUnverifiedEmailCallback
import jp.co.soramitsu.oauth.core.datasources.paywings.api.PayWingsResponse
import jp.co.soramitsu.oauth.core.datasources.paywings.impl.utils.parseToString
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class PaywingsChangeUnverifiedEmailUseCase @Inject constructor() {

    private var callback: ChangeUnverifiedEmailCallback? = null

    val changeUnverifiedEmailCallbackFlow =
        callbackFlow<PayWingsResponse> {
            callback = object : ChangeUnverifiedEmailCallback {
                override fun onError(error: OAuthErrorCode, errorMessage: String?) {
                    trySendBlocking(
                        PayWingsResponse.Error.OnChangeUnverifiedEmail(
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

                override fun onUserSignInRequired() {
                    trySendBlocking(
                        PayWingsResponse.NavigationIncentive.OnUserSignInRequiredScreen()
                    )
                }
            }

            awaitClose { callback = null }
        }

    suspend operator fun invoke(email: String) = callback?.let {
        PayWingsOAuthClient.instance.changeUnverifiedEmail(
            email = email,
            callback = it
        )
    }

}