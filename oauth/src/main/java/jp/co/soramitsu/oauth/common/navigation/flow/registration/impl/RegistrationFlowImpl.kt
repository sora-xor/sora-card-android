package jp.co.soramitsu.oauth.common.navigation.flow.registration.impl

import jp.co.soramitsu.oauth.common.navigation.activityresult.api.SoraCardResult
import jp.co.soramitsu.oauth.common.navigation.flow.registration.api.RegistrationDestination
import jp.co.soramitsu.oauth.common.navigation.flow.registration.api.RegistrationFlow
import jp.co.soramitsu.oauth.common.navigation.activityresult.api.ActivityResult
import jp.co.soramitsu.oauth.common.navigation.router.api.ComposeRouter
import javax.inject.Inject

class RegistrationFlowImpl @Inject constructor(
    private val composeRouter: ComposeRouter,
    private val activityResult: ActivityResult
): RegistrationFlow {

    override fun onStart(destination: RegistrationDestination) =
        when (destination) {
            is RegistrationDestination.EnterFirstAndLastName ->
                composeRouter.setNewStartDestination("ENTER_FIRST_AND_LAST_NAME")
            is RegistrationDestination.EnterEmail ->
                composeRouter.setNewStartDestination("ENTER_EMAIL")
            is RegistrationDestination.EmailConfirmation ->
                composeRouter.setNewStartDestination("SEND_VERIFICATION_EMAIL")
        }.run { composeRouter.clearBackStack() }

    override fun onBack() {
        composeRouter.popBack()
    }

    override fun onExit() {
        activityResult.setResult(SoraCardResult.Canceled)
    }

    override fun onEnterEmail() {
        composeRouter.navigateTo("ENTER_EMAIL")
        TODO("Add arguments")
    }
}