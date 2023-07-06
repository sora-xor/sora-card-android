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
import jp.co.soramitsu.oauth.core.engines.rest.api.RestException
import jp.co.soramitsu.oauth.core.engines.rest.api.parseToError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.StringJoiner
import javax.inject.Inject

class UserInteractorImpl @Inject constructor(
    private val inMemoryRepo: InMemoryRepo,
    private val tachiRepository: TachiRepository,
    private val payWingsRepository: PayWingsRepository,
    private val userSessionRepository: UserSessionRepository
): UserInteractor {

    private val header by lazy {
        StringJoiner(HEADER_DELIMITER).apply {
            add(inMemoryRepo.client)
            add(Build.MANUFACTURER)
            add(Build.VERSION.SDK_INT.toString())
        }.toString()
    }

    override val resultFlow: Flow<UserOperationResult> = payWingsRepository.responseFlow
        .filter {
            it is PayWingsResponse.Error.OnGetUserData
                    || it is PayWingsResponse.Result.ReceivedUserData
        }.map { payWingsResponse ->
            return@map when(payWingsResponse) {
                is PayWingsResponse.Result -> {
                    if (payWingsResponse is PayWingsResponse.Result.ReceivedUserData) {
                        val (accessToken, refreshToken) = userSessionRepository.run {
                            getAccessToken() to getRefreshToken()
                        }


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
                            text = Text.SimpleText(payWingsResponse.errorMessage)
                        )
                    } else UserOperationResult.Idle
                }

                else -> { UserOperationResult.Idle }
            }
        }.catch { throwable ->
            with(throwable) {
                if (this !is RestException) {
                    UserOperationResult.Error(
                        text = Text.StringRes(
                            id = R.string.cant_fetch_data
                        )
                    ).also { emit(it) }

                    return@with
                }

                UserOperationResult.Error(
                    text = Text.SimpleText(
                        text = parseToError()
                    )
                ).also { emit(it) }
            }
        }.flowOn(Dispatchers.IO)
        .filter { it !is UserOperationResult.Idle }

    override suspend fun getUserData() {
        userSessionRepository.getAccessToken().run {
            payWingsRepository.getUserData(
                accessToken = this
            )
        }
    }

    override suspend fun calculateFreeKycAttemptsLeft(): Result<Int> {
        val accessToken = userSessionRepository.getAccessToken()

        return tachiRepository.getFreeKycAttemptsInfo(header, accessToken)
            .map { it.total - it.completed }
    }

    private companion object {
        const val HEADER_DELIMITER = "/"
    }
}