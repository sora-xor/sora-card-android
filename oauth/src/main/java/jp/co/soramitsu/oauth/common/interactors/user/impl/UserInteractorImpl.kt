package jp.co.soramitsu.oauth.common.interactors.user.impl

import android.os.Build
import com.paywings.onboarding.kyc.android.sdk.data.model.KycUserData
import com.paywings.onboarding.kyc.android.sdk.data.model.UserCredentials
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.theme.views.Text
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.common.interactors.user.api.UserInteractor
import jp.co.soramitsu.oauth.common.interactors.user.api.UserOperationResult
import jp.co.soramitsu.oauth.core.datasources.paywings.api.PayWingsRepository
import jp.co.soramitsu.oauth.core.datasources.paywings.api.PayWingsResponse
import jp.co.soramitsu.oauth.core.datasources.session.api.UserSessionRepository
import jp.co.soramitsu.oauth.core.datasources.tachi.api.TachiRepository
import jp.co.soramitsu.oauth.core.engines.coroutines.api.CoroutinesStorage
import jp.co.soramitsu.oauth.core.engines.rest.api.RestException
import jp.co.soramitsu.oauth.core.engines.rest.api.parseToError
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.StringJoiner
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UserInteractorImpl @Inject constructor(
    private val inMemoryRepo: InMemoryRepo,
    private val tachiRepository: TachiRepository,
    private val payWingsRepository: PayWingsRepository,
    private val userSessionRepository: UserSessionRepository,
    private val coroutinesStorage: CoroutinesStorage
): UserInteractor {

    private val header by lazy {
        StringJoiner(HEADER_DELIMITER).apply {
            add(inMemoryRepo.client)
            add(Build.MANUFACTURER)
            add(Build.VERSION.SDK_INT.toString())
        }.toString()
    }

    override val resultFlow: StateFlow<UserOperationResult> =
        payWingsRepository.responseFlow
            .filter {
                it is PayWingsResponse.Error.OnGetUserData
                        || it is PayWingsResponse.Result.ReceivedUserData
            }.map { payWingsResponse ->
                try {
                    when(payWingsResponse) {
                        is PayWingsResponse.Result -> {
                            if (payWingsResponse is PayWingsResponse.Result.ReceivedUserData) {
                                val (accessToken, refreshToken) = userSessionRepository.run {
                                    getAccessToken() to getRefreshToken()
                                } // TODO check if refresh token is used even if accessToken is expired


                                val kycReferenceNumber = tachiRepository.getReferenceNumber(
                                    header = header,
                                    accessToken = accessToken,
                                    phoneNumber = payWingsResponse.phoneNumber,
                                    email = payWingsResponse.email
                                ).getOrThrow()

                                val kycUserData = KycUserData(
                                    firstName = payWingsResponse.firstName,
                                    lastName = payWingsResponse.lastName,
                                    email = payWingsResponse.email,
                                    mobileNumber = payWingsResponse.phoneNumber
                                )

                                val userCredentials = UserCredentials(
                                    accessToken = accessToken,
                                    refreshToken = refreshToken
                                )

                                UserOperationResult.ContractData(
                                    kycUserData = kycUserData,
                                    userCredentials = userCredentials,
                                    kycReferenceNumber = kycReferenceNumber
                                )
                            } else UserOperationResult.Idle
                        }

                        is PayWingsResponse.Error -> {
                            if (payWingsResponse is PayWingsResponse.Error.OnGetUserData) {
                                UserOperationResult.Error(
                                    text = payWingsResponse.errorText
                                )
                            } else UserOperationResult.Idle
                        }

                        else -> { UserOperationResult.Idle }
                    }
                } catch (throwable: Throwable) {
                    if (throwable is RestException) {
                        UserOperationResult.Error(
                            text = Text.SimpleText(
                                text = throwable.parseToError()
                            )
                        )
                    }

                    UserOperationResult.Error(
                        text = Text.StringRes(
                            id = R.string.cant_fetch_data
                        )
                    )
                }
            }.filter {
                it !is UserOperationResult.Idle
            }.stateIn(
                coroutinesStorage.supervisedIoScope,
                SharingStarted.WhileSubscribed(),
                UserOperationResult.Idle
            )

    override suspend fun getUserData() {
        val (accessToken, accessTokenExpirationTime) = userSessionRepository.run {
            getAccessToken() to getAccessTokenExpirationTime()
        }

        if (accessToken.isBlank() ||
            accessTokenExpirationTime <= TimeUnit.MILLISECONDS
                .toSeconds(System.currentTimeMillis()))
            throw RuntimeException(ACCESS_TOKEN_EXPIRED)

        payWingsRepository.getUserData(accessToken = accessToken)
    }

    override suspend fun calculateFreeKycAttemptsLeft(): Result<Int> {
        val (accessToken, accessTokenExpirationTime) = userSessionRepository.run {
            getAccessToken() to getAccessTokenExpirationTime()
        }

        if (accessToken.isBlank() ||
            accessTokenExpirationTime <= TimeUnit.MILLISECONDS
                .toSeconds(System.currentTimeMillis()))
            return Result.failure(RuntimeException(ACCESS_TOKEN_EXPIRED))

        return tachiRepository.getFreeKycAttemptsInfo(header, accessToken)
            .map { it.total - it.completed }
    }

    private companion object {
        const val HEADER_DELIMITER = "/"

        const val ACCESS_TOKEN_EXPIRED =
            "Access token has been expired, be sure to retrieve new one before proceeding"
    }
}