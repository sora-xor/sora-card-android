package jp.co.soramitsu.oauth.common.interactors.account.api

import kotlinx.coroutines.flow.SharedFlow

interface AccountInteractor {

    val resultFlow: SharedFlow<AccountOperationResult>

    suspend fun checkKycVerificationStatus()

    suspend fun requestOtpCode(phoneNumber: String)

    suspend fun resendOtpCode()

    suspend fun verifyOtpCode(otpCode: String)

    suspend fun registerUser(
        firstName: String,
        lastName: String,
        email: String
    )

    suspend fun checkEmailVerificationStatus()

    suspend fun requestNewVerificationEmail()

    suspend fun changeUnverifiedEmail(newEmail: String)

    suspend fun logOut()

}