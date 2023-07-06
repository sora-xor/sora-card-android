package jp.co.soramitsu.oauth.common.navigation.flow.registration.impl

import jp.co.soramitsu.oauth.core.engines.activityresult.api.SoraCardResult
import jp.co.soramitsu.oauth.common.navigation.flow.registration.api.RegistrationDestination
import jp.co.soramitsu.oauth.common.navigation.flow.registration.api.RegistrationFlow
import jp.co.soramitsu.oauth.core.engines.activityresult.api.ActivityResult
import jp.co.soramitsu.oauth.core.engines.router.api.ComposeRouter
import jp.co.soramitsu.oauth.core.engines.router.api.SoraCardDestinations
import javax.inject.Inject

class RegistrationFlowImpl @Inject constructor(
    private val composeRouter: ComposeRouter,
    private val activityResult: ActivityResult
): RegistrationFlow {

    override fun onStart(destination: RegistrationDestination) =
        when (destination) {
            is RegistrationDestination.EnterFirstAndLastName ->
                composeRouter.setNewStartDestination(SoraCardDestinations.EnterFirstAndLastName)
            is RegistrationDestination.EnterEmail -> { /* DO NOTHING */ }
            is RegistrationDestination.EmailConfirmation ->
                composeRouter.setNewStartDestination(
                    SoraCardDestinations.SendVerificationEmail(
                        email = destination.email,
                        autoEmailBeenSent = destination.autoEmailBeenSent
                    )
                )
        }.run { composeRouter.clearBackStack() }

    override fun onBack() {
        composeRouter.popBack()
    }

    override fun onExit() {
        activityResult.setResult(SoraCardResult.Canceled)
    }

    override fun onEnterEmail(firstName: String, lastName: String) {
        composeRouter.navigateTo(
            SoraCardDestinations.EnterEmail(
                firstName = firstName,
                lastName = lastName
            )
        )
    }
}