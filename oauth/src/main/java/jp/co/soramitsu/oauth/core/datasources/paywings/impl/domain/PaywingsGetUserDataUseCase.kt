package jp.co.soramitsu.oauth.core.datasources.paywings.impl.domain

import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import com.paywings.oauth.android.sdk.service.callback.GetUserDataCallback
import jp.co.soramitsu.oauth.core.datasources.paywings.api.PayWingsResponse
import jp.co.soramitsu.oauth.core.datasources.paywings.impl.utils.parseToString
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class PaywingsGetUserDataUseCase @Inject constructor() {

    private var callback: GetUserDataCallback? = null

    val getUserDataCallbackFlow =
        callbackFlow {
            callback = object : GetUserDataCallback {
                override fun onError(error: OAuthErrorCode, errorMessage: String?) {
                    trySendBlocking(
                        PayWingsResponse.Error.OnGetUserData(
                            errorMessage = errorMessage ?: error.parseToString()
                        )
                    )
                }

                override fun onUserData(
                    userId: String,
                    firstName: String?,
                    lastName: String?,
                    email: String?,
                    emailConfirmed: Boolean,
                    phoneNumber: String?
                ) {
                    trySendBlocking(
                        PayWingsResponse.Result.ReceivedUserData(
                            userId = userId,
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            emailConfirmed = emailConfirmed,
                            phoneNumber = phoneNumber
                        )
                    )
                }
            }

            awaitClose { callback = null }
        }

    suspend operator fun invoke(accessToken: String) = callback?.let {
        PayWingsOAuthClient.instance.getUserData(
            accessToken = accessToken,
            callback = it
        )
    }

}