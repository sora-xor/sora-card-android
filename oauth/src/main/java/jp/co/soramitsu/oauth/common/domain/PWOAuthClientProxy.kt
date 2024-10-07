package jp.co.soramitsu.oauth.common.domain

import android.content.Context
import com.paywings.oauth.android.sdk.data.enums.HttpRequestMethod
import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.data.model.GetNewAuthorizationDataResult
import com.paywings.oauth.android.sdk.initializer.OAuthInitializationCallback
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import com.paywings.oauth.android.sdk.service.callback.ChangeUnverifiedEmailCallback
import com.paywings.oauth.android.sdk.service.callback.CheckEmailVerifiedCallback
import com.paywings.oauth.android.sdk.service.callback.GetUserDataCallback
import com.paywings.oauth.android.sdk.service.callback.RegisterUserCallback
import com.paywings.oauth.android.sdk.service.callback.SendNewVerificationEmailCallback
import com.paywings.oauth.android.sdk.service.callback.SignInWithPhoneNumberRequestOtpCallback
import com.paywings.oauth.android.sdk.service.callback.SignInWithPhoneNumberVerifyOtpCallback
import jp.co.soramitsu.oauth.base.sdk.SoraCardEnvironmentType
import jp.co.soramitsu.oauth.base.sdk.toPayWingsType
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface PWOAuthClientProxy {
    suspend fun init(
        context: Context,
        type: SoraCardEnvironmentType,
        key: String,
        domain: String,
        platform: String,
        recaptcha: String,
    ): Pair<Boolean, String>

    suspend fun isSignIn(): Boolean
    suspend fun logout()

    suspend fun signInWithPhoneNumberVerifyOtp(
        otp: String,
        callback: SignInWithPhoneNumberVerifyOtpCallback,
    )

    suspend fun signInWithPhoneNumberRequestOtp(
        countryCode: String,
        phoneNumber: String,
        smsContentTemplate: String? = null,
        callback: SignInWithPhoneNumberRequestOtpCallback,
    )

    suspend fun checkEmailVerified(callback: CheckEmailVerifiedCallback)

    suspend fun sendNewVerificationEmail(callback: SendNewVerificationEmailCallback)

    suspend fun registerUser(
        firstName: String,
        lastName: String,
        email: String,
        callback: RegisterUserCallback,
    )

    suspend fun changeUnverifiedEmail(email: String, callback: ChangeUnverifiedEmailCallback)

    suspend fun getUserData(callback: GetUserDataCallback)

    suspend fun getNewAccessToken(url: String, method: String): GetNewAuthorizationDataResult
}

internal class PWOAuthClientProxyImpl : PWOAuthClientProxy {

    @Volatile
    private var initialized = false
    private val mutex = Mutex()

    override suspend fun init(
        context: Context,
        type: SoraCardEnvironmentType,
        key: String,
        domain: String,
        platform: String,
        recaptcha: String,
    ): Pair<Boolean, String> {
        val initResult: CompletableDeferred<Pair<Boolean, String>> = CompletableDeferred()
        val initResult2: CompletableDeferred<Pair<Boolean, String>> = CompletableDeferred()
        return if (initialized.not()) {
            mutex.withLock {
                if (initialized.not()) {
                    initInternal(context, type, key, domain, platform, recaptcha, initResult)
                    val res1 = initResult.await()
                    if (res1.first) {
                        res1
                    } else {
                        initInternal(context, type, key, domain, platform, recaptcha, initResult2)
                        initResult2.await()
                    }
                } else {
                    true to ""
                }
            }
        } else {
            true to ""
        }
    }

    private suspend fun initInternal(
        context: Context,
        type: SoraCardEnvironmentType,
        key: String,
        domain: String,
        platform: String,
        recaptcha: String,
        result: CompletableDeferred<Pair<Boolean, String>>,
    ) {
        PayWingsOAuthClient.init(
            context = context,
            environmentType = type.toPayWingsType(),
            apiKey = key,
            domain = domain,
            appPlatformID = platform,
            recaptchaKey = recaptcha,
            callback = object : OAuthInitializationCallback {
                override fun onFailure(error: OAuthErrorCode, errorMessage: String?) {
                    result.complete(false to (errorMessage ?: error.description))
                }

                override fun onSuccess() {
                    initialized = true
                    result.complete(true to "")
                }
            },
        )
    }

    override suspend fun isSignIn(): Boolean = PayWingsOAuthClient.instance.isUserSignIn()
    override suspend fun logout() = PayWingsOAuthClient.instance.signOutUser()

    override suspend fun signInWithPhoneNumberVerifyOtp(
        otp: String,
        callback: SignInWithPhoneNumberVerifyOtpCallback,
    ) {
        PayWingsOAuthClient.instance.signInWithPhoneNumberVerifyOtp(
            otp = otp,
            callback = callback,
        )
    }

    override suspend fun signInWithPhoneNumberRequestOtp(
        countryCode: String,
        phoneNumber: String,
        smsContentTemplate: String?,
        callback: SignInWithPhoneNumberRequestOtpCallback,
    ) {
        PayWingsOAuthClient.instance.signInWithPhoneNumberRequestOtp(
            phoneNumberCountryCode = countryCode,
            phoneNumber = phoneNumber,
            smsContentTemplate = smsContentTemplate,
            callback = callback,
        )
    }

    override suspend fun checkEmailVerified(callback: CheckEmailVerifiedCallback) {
        if (initialized.not()) return
        PayWingsOAuthClient.instance.checkEmailVerified(callback)
    }

    override suspend fun sendNewVerificationEmail(callback: SendNewVerificationEmailCallback) {
        PayWingsOAuthClient.instance.sendNewVerificationEmail(callback)
    }

    override suspend fun registerUser(
        firstName: String,
        lastName: String,
        email: String,
        callback: RegisterUserCallback,
    ) {
        PayWingsOAuthClient.instance.registerUser(
            firstName,
            lastName,
            email,
            callback,
        )
    }

    override suspend fun changeUnverifiedEmail(
        email: String,
        callback: ChangeUnverifiedEmailCallback,
    ) {
        PayWingsOAuthClient.instance.changeUnverifiedEmail(
            email,
            callback,
        )
    }

    override suspend fun getUserData(callback: GetUserDataCallback) {
        PayWingsOAuthClient.instance.getUserData(callback)
    }

    override suspend fun getNewAccessToken(
        url: String,
        method: String,
    ): GetNewAuthorizationDataResult = PayWingsOAuthClient.instance.getNewAuthorizationData(
        methodUrl = url,
        httpRequestMethod = HttpRequestMethod.getByName(method),
    )
}
