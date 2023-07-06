package jp.co.soramitsu.oauth.common.interactors.account.impl

import android.os.Build
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.theme.views.Text
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.common.interactors.account.api.AccountInteractor
import jp.co.soramitsu.oauth.common.interactors.account.api.AccountOperationResult
import jp.co.soramitsu.oauth.core.datasources.tachi.api.models.KycStatus
import jp.co.soramitsu.oauth.core.datasources.paywings.api.PayWingsRepository
import jp.co.soramitsu.oauth.core.datasources.paywings.api.PayWingsResponse
import jp.co.soramitsu.oauth.core.datasources.session.api.UserSessionRepository
import jp.co.soramitsu.oauth.core.datasources.tachi.api.TachiRepository
import jp.co.soramitsu.oauth.core.engines.rest.api.RestException
import jp.co.soramitsu.oauth.core.engines.rest.api.parseToError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import java.util.StringJoiner
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AccountInteractorImpl @Inject constructor(
    private val inMemoryRepo: InMemoryRepo,
    private val tachiRepository: TachiRepository,
    private val payWingsRepository: PayWingsRepository,
    private val userSessionRepository: UserSessionRepository
): AccountInteractor {

    private val header by lazy {
        StringJoiner(HEADER_DELIMITER).apply {
            add(inMemoryRepo.client)
            add(Build.MANUFACTURER)
            add(Build.VERSION.SDK_INT.toString())
        }.toString()
    }

    private val cache: MutableMap<String, Any> = mutableMapOf()

    override val resultFlow: Flow<AccountOperationResult.Error> = payWingsRepository.responseFlow
        .onStart {
            println("This is checkpoint: accountInteractor.resultFlow.onStart")
        }.onEach {
            println("This is checkpoint: resultFlow.payWingsResponse - $it")
        }.onCompletion {
            println("This is checkpoint: accountInteractor.resultFlow.onCompletion")
        }.filter {
            val result = it !is PayWingsResponse.NavigationIncentive
                    && it !is PayWingsResponse.Error.OnGetUserData
                    && it !is PayWingsResponse.Result.ReceivedUserData

            println("This is checkpoint: payWingsResponse filter result - $result")

            result
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
                                tachiRepository.getKycStatus(header, it.accessToken).getOrThrow()
                                    ?: KycStatus.NotInitialized

                            userSessionRepository.setKycStatus(kycStatusToSave)

                            AccountOperationResult.Executed
                        }

                        is PayWingsResponse.Result.ReceivedNewAccessToken -> {
                            userSessionRepository.setNewAccessToken(
                                accessToken = it.accessToken,
                                expirationTime = it.accessTokenExpirationTime
                            )

                            val kycStatusToSave =
                                tachiRepository.getKycStatus(header, it.accessToken).getOrThrow()
                                    ?: KycStatus.NotInitialized

                            userSessionRepository.setKycStatus(kycStatusToSave)

                            AccountOperationResult.Executed
                        }

                        is PayWingsResponse.Result.ReceivedUserData -> {
                            AccountOperationResult.Executed // is handled in UserInteractor
                        }

                        is PayWingsResponse.Result.ResendDelayedOtpRepeatedly -> {
                            delay(OTP_VERIFICATION_STATUS_DELAY_IN_MILLS)
                            requestOtpCode(
                                phoneNumber = cache[PHONE_NUMBER] as String
                            )

                            AccountOperationResult.Executed

                            // TODO launch delay in separate coroutine
                        }

                        is PayWingsResponse.Result.ResendDelayedVerificationEmailRepeatedly -> {
                            delay(EMAIL_VERIFICATION_STATUS_DELAY_IN_MILLS)
                            checkEmailVerificationStatus()

                            AccountOperationResult.Executed

                            // TODO launch delay in separate coroutine
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
        }.catch { throwable ->
            with(throwable) {
                if (this !is RestException) {
                    AccountOperationResult.Error(
                        text = Text.StringRes(
                            id = R.string.cant_fetch_data
                        )
                    ).also { emit(it) }

                    return@with
                }

                AccountOperationResult.Error(
                    text = Text.SimpleText(
                        text = parseToError()
                    )
                ).also { emit(it) }
            }
        }.flowOn(Dispatchers.IO)
        .filterIsInstance()

    override suspend fun checkKycVerificationStatus() {
        val (accessToken, accessTokenExpirationTime, refreshToken) =
            userSessionRepository.run {
                Triple(getAccessToken(), getAccessTokenExpirationTime(), getRefreshToken())
            }

        if (accessToken.isBlank() ||
            accessTokenExpirationTime <= TimeUnit.MILLISECONDS
                .toSeconds(System.currentTimeMillis())
        ) {
            if (refreshToken.isNotBlank())
                payWingsRepository.getAccessToken(refreshToken) else
                    payWingsRepository.checkEmailStatus()
            return
        }

        val kycStatusToSave =
            tachiRepository.getKycStatus(header, accessToken).getOrThrow()
                ?: KycStatus.NotInitialized

        userSessionRepository.setKycStatus(kycStatusToSave)
    }

    override suspend fun requestOtpCode(phoneNumber: String) {
        payWingsRepository.requestOtpCode(phoneNumber).apply {
            cache[PHONE_NUMBER] = phoneNumber
        }
    }

    override suspend fun verifyOtpCode(otpCode: String) {
        payWingsRepository.verifyPhoneNumberWithOtp(otpCode)
    }

    override suspend fun registerUser(firstName: String, lastName: String, email: String) {
        payWingsRepository.registerUser(firstName, lastName, email).apply {
            cache[FIRST_NAME] = firstName
            cache[LAST_NAME] = lastName
            cache[EMAIL] = email
        }
    }

    override suspend fun checkEmailVerificationStatus() {
        payWingsRepository.checkEmailStatus()
    }

    override suspend fun requestNewVerificationEmail() {
        payWingsRepository.sendVerificationEmail()
    }

    override suspend fun changeUnverifiedEmail(newEmail: String) {
        payWingsRepository.changeUnverifiedEmail(newEmail).apply {
            cache[EMAIL] = newEmail
        }
    }

    override suspend fun logOut() {
        // TODO add implementation
    }

    private companion object {
        const val HEADER_DELIMITER = "/"

        const val PHONE_NUMBER = "PHONE_NUMBER_KEY"
        const val FIRST_NAME = "FIRST_NAME_KEY"
        const val LAST_NAME = "LAST_NAME_KEY"
        const val EMAIL = "EMAIL_KEY"

        const val EMAIL_VERIFICATION_STATUS_DELAY_IN_MILLS = 5_000L
        const val OTP_VERIFICATION_STATUS_DELAY_IN_MILLS = 300L
    }
}