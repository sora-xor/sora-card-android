package jp.co.soramitsu.oauth.common.domain

import android.content.Context
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import com.paywings.oauth.android.sdk.service.callback.ChangeUnverifiedEmailCallback
import com.paywings.oauth.android.sdk.service.callback.CheckEmailVerifiedCallback
import com.paywings.oauth.android.sdk.service.callback.GetNewAccessTokenCallback
import com.paywings.oauth.android.sdk.service.callback.GetUserDataCallback
import com.paywings.oauth.android.sdk.service.callback.RegisterUserCallback
import com.paywings.oauth.android.sdk.service.callback.SendNewVerificationEmailCallback
import com.paywings.oauth.android.sdk.service.callback.SignInWithPhoneNumberRequestOtpCallback
import com.paywings.oauth.android.sdk.service.callback.SignInWithPhoneNumberVerifyOtpCallback
import jp.co.soramitsu.oauth.base.sdk.SoraCardEnvironmentType
import jp.co.soramitsu.oauth.base.sdk.toPayWingsType

interface PWOAuthClientProxy {
    fun init(context: Context, type: SoraCardEnvironmentType, key: String, domain: String)

    suspend fun signInWithPhoneNumberVerifyOtp(otp: String, callback: SignInWithPhoneNumberVerifyOtpCallback)

    suspend fun signInWithPhoneNumberRequestOtp(phoneNumber: String, smsContentTemplate: String? = null, callback: SignInWithPhoneNumberRequestOtpCallback)

    suspend fun checkEmailVerified(callback: CheckEmailVerifiedCallback)

    suspend fun sendNewVerificationEmail(callback: SendNewVerificationEmailCallback)

    suspend fun registerUser(firstName: String, lastName: String, email: String, callback: RegisterUserCallback)

    suspend fun changeUnverifiedEmail(email: String, callback: ChangeUnverifiedEmailCallback)

    suspend fun getUserData(accessToken: String, callback: GetUserDataCallback)

    suspend fun getNewAccessToken(refreshToken: String, callback: GetNewAccessTokenCallback)
}

internal class PWOAuthClientProxyImpl() : PWOAuthClientProxy {

    override fun init(
        context: Context,
        type: SoraCardEnvironmentType,
        key: String,
        domain: String
    ) {
        PayWingsOAuthClient.init(context, type.toPayWingsType(), key, domain)
    }

    override suspend fun signInWithPhoneNumberVerifyOtp(
        otp: String,
        callback: SignInWithPhoneNumberVerifyOtpCallback
    ) {
        PayWingsOAuthClient.instance.signInWithPhoneNumberVerifyOtp(
            otp = otp,
            callback = callback,
        )
    }

    override suspend fun signInWithPhoneNumberRequestOtp(
        phoneNumber: String,
        smsContentTemplate: String?,
        callback: SignInWithPhoneNumberRequestOtpCallback
    ) {
        PayWingsOAuthClient.instance.signInWithPhoneNumberRequestOtp(
            phoneNumber = phoneNumber,
            smsContentTemplate = smsContentTemplate,
            callback = callback,
        )
    }

    override suspend fun checkEmailVerified(callback: CheckEmailVerifiedCallback) {
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
            firstName, lastName, email, callback,
        )
    }

    override suspend fun changeUnverifiedEmail(
        email: String,
        callback: ChangeUnverifiedEmailCallback
    ) {
        PayWingsOAuthClient.instance.changeUnverifiedEmail(
            email, callback,
        )
    }

    override suspend fun getUserData(accessToken: String, callback: GetUserDataCallback) {
        PayWingsOAuthClient.instance.getUserData(accessToken, callback,)
    }

    override suspend fun getNewAccessToken(
        refreshToken: String,
        callback: GetNewAccessTokenCallback
    ) {
        PayWingsOAuthClient.instance.getNewAccessToken(
            refreshToken,
            callback,
        )
    }
}