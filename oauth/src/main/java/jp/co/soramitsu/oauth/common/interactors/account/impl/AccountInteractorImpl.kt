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
import jp.co.soramitsu.oauth.core.engines.coroutines.api.CoroutinesStorage
import jp.co.soramitsu.oauth.core.engines.rest.api.RestException
import jp.co.soramitsu.oauth.core.engines.rest.api.parseToError
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import java.util.StringJoiner
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AccountInteractorImpl @Inject constructor(
    private val inMemoryRepo: InMemoryRepo,
    private val tachiRepository: TachiRepository,
    private val payWingsRepository: PayWingsRepository,
    private val userSessionRepository: UserSessionRepository,
    private val coroutinesStorage: CoroutinesStorage
): AccountInteractor {

    private val header by lazy {
        StringJoiner(HEADER_DELIMITER).apply {
            add(inMemoryRepo.client)
            add(Build.MANUFACTURER)
            add(Build.VERSION.SDK_INT.toString())
        }.toString()
    }

    private val cache: MutableMap<String, Any> = mutableMapOf()

    override val resultFlow: SharedFlow<AccountOperationResult> =
        payWingsRepository.responseFlow
            .filter {
                it !is PayWingsResponse.NavigationIncentive
                        && it !is PayWingsResponse.Error.OnGetUserData
                        && it !is PayWingsResponse.Result.ReceivedUserData
            }.map {
                try {
                    when (it) {
                        is PayWingsResponse.Result -> {
                            when (it) {
                                is PayWingsResponse.Result.ReceivedAccessTokens -> {
                                    coroutinesStorage.supervisedIoScope.launch {
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
                                    }

                                    AccountOperationResult.Executed
                                }

                                is PayWingsResponse.Result.ReceivedNewAccessToken -> {
                                    coroutinesStorage.supervisedIoScope.launch {
                                        userSessionRepository.setNewAccessToken(
                                            accessToken = it.accessToken,
                                            expirationTime = it.accessTokenExpirationTime
                                        )

                                        val kycStatusToSave =
                                            tachiRepository.getKycStatus(header, it.accessToken).getOrThrow()
                                                ?: KycStatus.NotInitialized

                                        userSessionRepository.setKycStatus(kycStatusToSave)
                                    }

                                    AccountOperationResult.Executed
                                }

                                is PayWingsResponse.Result.ReceivedUserData -> {
                                    AccountOperationResult.Executed // is handled in UserInteractor
                                }

                                is PayWingsResponse.Result.ResendDelayedOtpRepeatedly -> {
                                    coroutinesStorage.supervisedIoScope.launch {
                                        delay(OTP_VERIFICATION_STATUS_DELAY_IN_MILLS)
                                        requestOtpCode(
                                            phoneNumber = cache[PHONE_NUMBER] as String
                                        )
                                    }

                                    AccountOperationResult.Loading
                                }

                                is PayWingsResponse.Result.ResendDelayedVerificationEmailRepeatedly -> {
                                    coroutinesStorage.supervisedIoScope.launch {
                                        delay(EMAIL_VERIFICATION_STATUS_DELAY_IN_MILLS)
                                        checkEmailVerificationStatus()
                                    }

                                    AccountOperationResult.Loading
                                }
                            }
                        }

                        is PayWingsResponse.Error -> {
                            when (it) {
                                is PayWingsResponse.Error.OnChangeUnverifiedEmail -> {
                                    AccountOperationResult.Error(
                                        text = it.errorText
                                    )
                                }

                                is PayWingsResponse.Error.OnCheckEmailVerification -> {
                                    AccountOperationResult.Error(
                                        text = it.errorText
                                    )
                                }

                                is PayWingsResponse.Error.OnGetNewAccessToken -> {
                                    AccountOperationResult.Error(
                                        text = it.errorText
                                    )
                                }

                                is PayWingsResponse.Error.OnGetUserData -> {
                                    AccountOperationResult.Executed  // is handled in UserInteractor
                                }

                                is PayWingsResponse.Error.OnSendNewVerificationEmail -> {
                                    AccountOperationResult.Error(
                                        text = it.errorText
                                    )
                                }

                                is PayWingsResponse.Error.OnSignInWithPhoneNumberVerifyOtp -> {
                                    AccountOperationResult.Error(
                                        text = it.errorText
                                    )
                                }

                                is PayWingsResponse.Error.OnRegisterUser -> {
                                    AccountOperationResult.Error(
                                        text = it.errorText
                                    )
                                }

                                is PayWingsResponse.Error.OnSignWithPhoneNumberRequestOtp -> {
                                    AccountOperationResult.Error(
                                        text = it.errorText
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

                        is PayWingsResponse.Loading -> {
                            AccountOperationResult.Loading
                        }

                        else -> { AccountOperationResult.Executed }
                    }
                } catch (throwable: Throwable) {
                    if (throwable is RestException) {
                        AccountOperationResult.Error(
                            text = Text.SimpleText(
                                text = throwable.parseToError()
                            )
                        )
                    }

                    AccountOperationResult.Error(
                        text = Text.StringRes(
                            id = R.string.cant_fetch_data
                        )
                    )
                }
            }.filter {
                it !is AccountOperationResult.Executed
            }.shareIn(
                coroutinesStorage.supervisedIoScope,
                SharingStarted.WhileSubscribed(),
                SHARED_FLOW_REPLAY_COUNT
            )

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

    override suspend fun resendOtpCode() {
        requestOtpCode(
            phoneNumber = cache[PHONE_NUMBER] as String
        )
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

        const val SHARED_FLOW_REPLAY_COUNT = 1
    }
}