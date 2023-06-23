package jp.co.soramitsu.oauth.common.interactors.user.impl

import com.paywings.onboarding.kyc.android.sdk.data.model.KycUserData
import com.paywings.onboarding.kyc.android.sdk.data.model.UserCredentials
import jp.co.soramitsu.oauth.base.compose.Text
import jp.co.soramitsu.oauth.common.interactors.user.api.UserInteractor
import jp.co.soramitsu.oauth.common.interactors.user.api.UserOperationResult
import jp.co.soramitsu.oauth.core.datasources.paywings.api.PayWingsRepository
import jp.co.soramitsu.oauth.core.datasources.paywings.api.PayWingsResponse
import jp.co.soramitsu.oauth.core.datasources.session.api.UserSessionRepository
import jp.co.soramitsu.oauth.core.datasources.tachi.api.KycRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserInteractorImpl @Inject constructor(
    private val kycRepository: KycRepository,
    private val payWingsRepository: PayWingsRepository,
    private val userSessionRepository: UserSessionRepository
): UserInteractor {

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


                        val kycReferenceNumber = kycRepository.getReferenceNumber(
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
        }.catch {
            emit(
                UserOperationResult.Error(
                    text = Text.SimpleText(text = "TODO")
                )
            )
            TODO()
        }.filter { it !is UserOperationResult.Idle }

    override suspend fun getUserData() {
        userSessionRepository.getAccessToken().run {
            payWingsRepository.getUserData(
                accessToken = this
            )
        }
    }
}