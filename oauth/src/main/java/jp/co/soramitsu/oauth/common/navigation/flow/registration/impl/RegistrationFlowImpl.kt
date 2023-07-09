package jp.co.soramitsu.oauth.common.navigation.flow.registration.impl

import android.os.Bundle
import androidx.core.os.bundleOf
import jp.co.soramitsu.oauth.common.navigation.flow.login.api.LoginDestination
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

    private val _args = mutableMapOf<String, Bundle>()

    override val args: Map<String, Bundle> = _args

    override fun onStart(destination: RegistrationDestination) =
        when (destination) {
            is RegistrationDestination.EnterFirstAndLastName ->
                composeRouter.setNewStartDestination(destination)
            is RegistrationDestination.EnterEmail -> { /* DO NOTHING */ }
            is RegistrationDestination.EmailConfirmation -> {
                _args[RegistrationDestination.EmailConfirmation::class.java.name] = Bundle().apply {
                    putString(
                        RegistrationDestination.EmailConfirmation.EMAIL_KEY,
                        destination.email
                    )
                    putBoolean(
                        RegistrationDestination.EmailConfirmation.AUTO_EMAIL_BEEN_SENT_KEY,
                        destination.autoEmailBeenSent
                    )
                }
                composeRouter.setNewStartDestination(destination)
            }
        }

    override fun onBack() {
        composeRouter.popBack()
    }

    override fun onExit() {
        activityResult.setResult(SoraCardResult.Canceled)
    }

    override fun onEnterEmail(firstName: String, lastName: String) {
        _args[RegistrationDestination.EnterEmail::class.java.name] = Bundle().apply {
            putString(
                RegistrationDestination.EnterEmail.FIRST_NAME_KEY,
                firstName
            )
            putString(
                RegistrationDestination.EnterEmail.LAST_NAME_KEY,
                lastName
            )
        }
        composeRouter.navigateTo(
            RegistrationDestination.EnterEmail(
                firstName = firstName,
                lastName = lastName
            )
        )
    }
}