package jp.co.soramitsu.oauth.common.navigation.coordinator.impl

import jp.co.soramitsu.oauth.core.datasources.tachi.api.KycStatus
import jp.co.soramitsu.oauth.common.navigation.flow.login.api.LoginDestination
import jp.co.soramitsu.oauth.common.navigation.coordinator.api.NavigationCoordinator
import jp.co.soramitsu.oauth.common.navigation.flow.registration.api.RegistrationDestination
import jp.co.soramitsu.oauth.common.navigation.flow.login.api.LoginFlow
import jp.co.soramitsu.oauth.common.navigation.flow.registration.api.RegistrationFlow
import jp.co.soramitsu.oauth.common.navigation.flow.verification.api.VerificationDestination
import jp.co.soramitsu.oauth.common.navigation.flow.verification.api.VerificationFlow
import jp.co.soramitsu.oauth.core.datasources.paywings.api.PayWingsRepository
import jp.co.soramitsu.oauth.core.datasources.paywings.api.PayWingsResponse
import jp.co.soramitsu.oauth.core.datasources.session.api.UserSessionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

class NavigationCoordinatorImpl @Inject constructor(
    payWingsRepository: PayWingsRepository,
    userSessionRepository: UserSessionRepository,
    private val loginFlow: LoginFlow,
    private val registrationFlow: RegistrationFlow,
    private val verificationFlow: VerificationFlow,
): NavigationCoordinator {

    private val kycAndVerificationFlow = userSessionRepository.kycStatusFlow
        .map { kycStatus ->
            when(kycStatus) {
                KycStatus.FirstInitialization -> {
                    verificationFlow.onStart(
                        destination = VerificationDestination.Start
                    )
                }
                KycStatus.Started -> {
                    verificationFlow.onStart(
                        destination = VerificationDestination.Start
                    )
                }
                KycStatus.Failed -> {
                    verificationFlow.onStart(
                        destination = VerificationDestination.Start
                    )
                }
                KycStatus.Completed -> {
                    verificationFlow.onStart(
                        destination = VerificationDestination.VerificationInProgress
                    )
                }
                KycStatus.Rejected -> {
                    verificationFlow.onStart(
                        destination = VerificationDestination.VerificationRejected
                    )
                }
                KycStatus.Successful -> {
                    verificationFlow.onStart(
                        destination = VerificationDestination.VerificationSuccessful
                    )
                }
            }
        }

    private val loginAndRegistrationFlow = payWingsRepository.responseFlow
        .filter {
            it is PayWingsResponse.NavigationIncentive
                    || it is PayWingsResponse.Error.OnGetNewAccessToken
        }.map { payWingsResponse ->
            when(payWingsResponse) {
                is PayWingsResponse.Error -> {
                    if (payWingsResponse is PayWingsResponse.Error.OnGetNewAccessToken)
                        loginFlow.onStart(
                            destination = LoginDestination.EnterPhone
                        )
                    else { /* DO NOTHING */ }
                }

                is PayWingsResponse.NavigationIncentive -> {
                    when(payWingsResponse) {
                        is PayWingsResponse.NavigationIncentive.OnUserSignInRequiredScreen -> {
                            loginFlow.onStart(
                                destination = LoginDestination.EnterPhone
                            )
                        }
                        is PayWingsResponse.NavigationIncentive.OnVerificationOtpBeenSent -> {
                            loginFlow.onStart(
                                destination = LoginDestination.EnterOtp
                            )
                        }
                        is PayWingsResponse.NavigationIncentive.OnRegistrationRequiredScreen -> {
                            registrationFlow.onStart(
                                destination = RegistrationDestination.EnterFirstAndLastName
                            )
                        }
                        is PayWingsResponse.NavigationIncentive.OnEmailConfirmationRequiredScreen -> {
                            registrationFlow.onStart(
                                destination = RegistrationDestination.EmailConfirmation
                            )
                        }
                    }
                }

                else -> { /* DO NOTHING */ }
            }
        }

    override fun start(coroutineScope: CoroutineScope) =
        merge(loginAndRegistrationFlow, kycAndVerificationFlow).launchIn(coroutineScope)

}