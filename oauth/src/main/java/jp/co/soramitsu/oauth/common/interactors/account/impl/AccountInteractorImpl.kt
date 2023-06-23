package jp.co.soramitsu.oauth.common.interactors.account.impl

import jp.co.soramitsu.oauth.base.compose.Text
import jp.co.soramitsu.oauth.common.interactors.account.api.AccountInteractor
import jp.co.soramitsu.oauth.common.interactors.account.api.AccountOperationResult
import jp.co.soramitsu.oauth.core.datasources.tachi.api.KycStatus
import jp.co.soramitsu.oauth.core.datasources.paywings.api.PayWingsRepository
import jp.co.soramitsu.oauth.core.datasources.paywings.api.PayWingsResponse
import jp.co.soramitsu.oauth.core.datasources.session.api.UserSessionRepository
import jp.co.soramitsu.oauth.core.datasources.tachi.api.KycRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccountInteractorImpl @Inject constructor(
    private val kycRepository: KycRepository,
    private val payWingsRepository: PayWingsRepository,
    private val userSessionRepository: UserSessionRepository
): AccountInteractor {

    override val resultFlow: Flow<AccountOperationResult.Error> = payWingsRepository.responseFlow
        .filter {
            it !is PayWingsResponse.NavigationIncentive
                    && it !is PayWingsResponse.Error.OnGetUserData
                    && it !is PayWingsResponse.Result.ReceivedUserData
        }.map {
            return@map when (it) {
                is PayWingsResponse.Result -> {
                    when (it) {
                        is PayWingsResponse.Result.ReceivedAccessTokens -> {
                            userSessionRepository.setNewAccessToken(
                                accessToken = it.accessToken,
                                expirationTime = it.accessTokenExpirationTime
                            )
                            userSessionRepository.setRefreshToken(
                                refreshToken = it.refreshToken
                            )

                            val kycStatusToSave =
                                kycRepository.getKycStatus(it.accessToken).getOrThrow()
                                    ?: KycStatus.FirstInitialization

                            userSessionRepository.setKycStatus(kycStatusToSave)

                            AccountOperationResult.Executed
                        }

                        is PayWingsResponse.Result.ReceivedNewAccessToken -> {
                            userSessionRepository.setNewAccessToken(
                                accessToken = it.accessToken,
                                expirationTime = it.accessTokenExpirationTime
                            )

                            AccountOperationResult.Executed
                        }

                        is PayWingsResponse.Result.ReceivedUserData -> {
                            AccountOperationResult.Executed // is handled in UserInteractor
                        }

                        is PayWingsResponse.Result.ResendDelayedOtpRepeatedly -> {
                            delay(OTP_VERIFICATION_STATUS_DELAY_IN_MILLS)
                            requestOtpCode(
                                phoneNumber = ""
                            )

                            AccountOperationResult.Executed

                            TODO("launch delay in separate coroutine")
                        }

                        is PayWingsResponse.Result.ResendDelayedVerificationEmailRepeatedly -> {
                            delay(EMAIL_VERIFICATION_STATUS_DELAY_IN_MILLS)
                            checkEmailVerificationStatus()

                            AccountOperationResult.Executed

                            TODO("launch delay in separate coroutine")
                        }
                    }
                }

                is PayWingsResponse.Error -> {
                    when (it) {
                        is PayWingsResponse.Error.OnChangeUnverifiedEmail -> {
                            AccountOperationResult.Error(
                                text = Text.SimpleText(text = it.errorMessage)
                            )
                        }

                        is PayWingsResponse.Error.OnCheckEmailVerification -> {
                            AccountOperationResult.Error(
                                text = Text.SimpleText(text = it.errorMessage)
                            )
                        }

                        is PayWingsResponse.Error.OnGetNewAccessToken -> {
                            AccountOperationResult.Error(
                                text = Text.SimpleText(text = it.errorMessage)
                            )
                        }

                        is PayWingsResponse.Error.OnGetUserData -> {
                            AccountOperationResult.Executed  // is handled in UserInteractor
                        }

                        is PayWingsResponse.Error.OnSendNewVerificationEmail -> {
                            AccountOperationResult.Error(
                                text = Text.SimpleText(text = it.errorMessage)
                            )
                        }

                        is PayWingsResponse.Error.OnSignInWithPhoneNumberVerifyOtp -> {
                            AccountOperationResult.Error(
                                text = Text.SimpleText(text = it.errorMessage)
                            )
                        }

                        is PayWingsResponse.Error.OnRegisterUser -> {
                            AccountOperationResult.Error(
                                text = Text.SimpleText(text = it.errorMessage)
                            )
                        }

                        is PayWingsResponse.Error.OnSignWithPhoneNumberRequestOtp -> {
                            AccountOperationResult.Error(
                                text = Text.SimpleText(text = it.errorMessage)
                            )
                        }

                        is PayWingsResponse.Error.OnVerificationByOtpFailed -> {
                            AccountOperationResult.Error(
                                text = Text.StringRes(id = 0)
                            )
                            TODO("Add String Res Message")
                        }
                    }
                }

                else -> { AccountOperationResult.Executed }
            }
        }.catch {
            emit(
                AccountOperationResult.Error(
                    text = Text.SimpleText(text = "TODO")
                )
            )
            TODO()
        }.filterIsInstance()

    override suspend fun requestOtpCode(phoneNumber: String) {
        payWingsRepository.requestOtpCode(phoneNumber)
    }

    override suspend fun verifyOtpCode(otpCode: String) {
        payWingsRepository.verifyPhoneNumberWithOtp(otpCode)
    }

    override suspend fun registerUser(firstName: String, lastName: String, email: String) {
        payWingsRepository.registerUser(firstName, lastName, email)
    }

    override suspend fun checkEmailVerificationStatus() {
        payWingsRepository.checkEmailStatus()
    }

    override suspend fun requestNewVerificationEmail() {
        payWingsRepository.sendVerificationEmail()
    }

    override suspend fun changeUnverifiedEmail(newEmail: String) {
        payWingsRepository.changeUnverifiedEmail(newEmail)
    }

    override suspend fun logOut() {
        TODO("Not yet implemented")
    }

    private companion object {
        const val EMAIL_VERIFICATION_STATUS_DELAY_IN_MILLS = 5_000L
        const val OTP_VERIFICATION_STATUS_DELAY_IN_MILLS = 300L
    }
}