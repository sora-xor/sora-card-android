package jp.co.soramitsu.oauth.core.datasources.paywings.api

import kotlinx.coroutines.flow.Flow

interface PayWingsRepository {

    val responseFlow: Flow<PayWingsResponse>

    suspend fun changeUnverifiedEmail(email: String)

    suspend fun checkEmailStatus()

    suspend fun getAccessToken(refreshToken: String)

    suspend fun getUserData(accessToken: String)

    suspend fun registerUser(
        firstName: String,
        lastName: String,
        email: String
    )

    suspend fun requestOtpCode(phoneNumber: String)

    suspend fun sendVerificationEmail()

    suspend fun verifyPhoneNumberWithOtp(otpCode: String)

}