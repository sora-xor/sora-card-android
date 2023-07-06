package jp.co.soramitsu.oauth.common.interactors.account.api

import kotlinx.coroutines.flow.Flow

interface AccountInteractor {

    val resultFlow: Flow<AccountOperationResult.Error>

    suspend fun checkKycVerificationStatus()

    suspend fun requestOtpCode(phoneNumber: String)

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