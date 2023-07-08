package jp.co.soramitsu.oauth.core.datasources.paywings.impl

import jp.co.soramitsu.oauth.core.datasources.paywings.api.PayWingsRepository
import jp.co.soramitsu.oauth.core.datasources.paywings.api.PayWingsResponse
import jp.co.soramitsu.oauth.core.datasources.paywings.impl.domain.PaywingsChangeUnverifiedEmailUseCase
import jp.co.soramitsu.oauth.core.datasources.paywings.impl.domain.PaywingsCheckEmailStatusUseCase
import jp.co.soramitsu.oauth.core.datasources.paywings.impl.domain.PaywingsGetAccessTokenUseCase
import jp.co.soramitsu.oauth.core.datasources.paywings.impl.domain.PaywingsGetUserDataUseCase
import jp.co.soramitsu.oauth.core.datasources.paywings.impl.domain.PaywingsRegisterUserUseCase
import jp.co.soramitsu.oauth.core.datasources.paywings.impl.domain.PaywingsRequestOtpByPhoneNumberUseCase
import jp.co.soramitsu.oauth.core.datasources.paywings.impl.domain.PaywingsSendVerificationEmailUseCase
import jp.co.soramitsu.oauth.core.datasources.paywings.impl.domain.PaywingsSignInWithPhoneNumberUseCase
import jp.co.soramitsu.oauth.core.engines.coroutines.api.CoroutinesStorage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class PayWingsRepositoryImpl @Inject constructor(
    private val changeUnverifiedEmailUseCase: PaywingsChangeUnverifiedEmailUseCase,
    private val checkEmailStatusUseCase: PaywingsCheckEmailStatusUseCase,
    private val getAccessTokenUseCase: PaywingsGetAccessTokenUseCase,
    private val getUserDataUseCase: PaywingsGetUserDataUseCase,
    private val registerUserUseCase: PaywingsRegisterUserUseCase,
    private val requestOtpByPhoneNumberUseCase: PaywingsRequestOtpByPhoneNumberUseCase,
    private val sendVerificationEmailUseCase: PaywingsSendVerificationEmailUseCase,
    private val signInWithPhoneNumberUseCase: PaywingsSignInWithPhoneNumberUseCase,
    private val coroutinesStorage: CoroutinesStorage
): PayWingsRepository {

    override val responseFlow: StateFlow<PayWingsResponse> = merge(
        changeUnverifiedEmailUseCase.changeUnverifiedEmailCallbackFlow,
        checkEmailStatusUseCase.checkEmailVerifiedCallbackFlow,
        getAccessTokenUseCase.getNewAccessTokenCallbackFlow,
        getUserDataUseCase.getUserDataCallbackFlow,
        registerUserUseCase.registerUserCallbackFlow,
        requestOtpByPhoneNumberUseCase.requestOtpByPhoneNumberCallbackFlow,
        sendVerificationEmailUseCase.sendNewVerificationEmailCallbackFlow,
        signInWithPhoneNumberUseCase.signInWithPhoneNumberVerifyOtpCallbackFlow
    ).stateIn(
        coroutinesStorage.supervisedIoScope,
        SharingStarted.WhileSubscribed(STOP_STATES_EMISSION_AFTER),
        PayWingsResponse.Loading
    )

    override suspend fun changeUnverifiedEmail(email: String) {
        changeUnverifiedEmailUseCase.invoke(email)
    }

    override suspend fun checkEmailStatus() {
        checkEmailStatusUseCase.invoke()
    }

    override suspend fun getAccessToken(refreshToken: String) {
        getAccessTokenUseCase.invoke(refreshToken)
    }

    override suspend fun getUserData(accessToken: String) {
        getUserDataUseCase.invoke(accessToken)
    }

    override suspend fun registerUser(firstName: String, lastName: String, email: String) {
        registerUserUseCase.invoke(firstName, lastName, email)
    }

    override suspend fun requestOtpCode(phoneNumber: String) {
        requestOtpByPhoneNumberUseCase.invoke(phoneNumber)
    }

    override suspend fun sendVerificationEmail() {
        sendVerificationEmailUseCase.invoke()
    }

    override suspend fun verifyPhoneNumberWithOtp(otpCode: String) {
        signInWithPhoneNumberUseCase.invoke(otpCode)
    }

    private companion object {
        const val STOP_STATES_EMISSION_AFTER = 5_000L
    }
}