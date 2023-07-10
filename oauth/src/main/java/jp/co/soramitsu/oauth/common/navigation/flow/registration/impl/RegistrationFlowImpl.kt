package jp.co.soramitsu.oauth.common.navigation.flow.registration.impl

import android.os.Bundle
import jp.co.soramitsu.oauth.core.engines.activityresult.api.SoraCardResult
import jp.co.soramitsu.oauth.common.navigation.flow.registration.api.RegistrationDestination
import jp.co.soramitsu.oauth.common.navigation.flow.registration.api.RegistrationFlow
import jp.co.soramitsu.oauth.core.engines.activityresult.api.ActivityResult
import jp.co.soramitsu.oauth.core.engines.router.api.ComposeRouter
import jp.co.soramitsu.oauth.core.engines.router.api.SoraCardDestinations
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class RegistrationFlowImpl @Inject constructor(
    private val composeRouter: ComposeRouter,
    private val activityResult: ActivityResult
): RegistrationFlow {

    private val _argsFlow = MutableSharedFlow<Pair<SoraCardDestinations, Bundle>>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val argsFlow: SharedFlow<Pair<SoraCardDestinations, Bundle>> = _argsFlow

    override fun onStart(destination: RegistrationDestination) =
        when (destination) {
            is RegistrationDestination.EnterFirstAndLastName ->
                composeRouter.setNewStartDestination(destination)
            is RegistrationDestination.EnterEmail -> { /* DO NOTHING */ }
            is RegistrationDestination.EmailConfirmation -> {
                _argsFlow.tryEmit(
                    value = destination to Bundle().apply {
                        putString(
                            RegistrationDestination.EmailConfirmation.EMAIL_KEY, destination.email
                        )
                        putBoolean(
                            RegistrationDestination.EmailConfirmation.AUTO_EMAIL_BEEN_SENT_KEY,
                            destination.autoEmailBeenSent
                        )
                    }
                )
                composeRouter.setNewStartDestination(destination)
            }
        }

    override fun onBack() {
        composeRouter.popBack()
    }

    override fun onExit() {
        activityResult.setResult(SoraCardResult.Canceled)
    }

    override fun onLogout() {
        activityResult.setResult(SoraCardResult.Canceled)
    }

    override fun onEnterEmail(firstName: String, lastName: String) {
        _argsFlow.tryEmit(
            value = RegistrationDestination.EnterEmail to Bundle().apply {
                putString(
                    RegistrationDestination.EnterEmail.FIRST_NAME_KEY, firstName
                )
                putString(
                    RegistrationDestination.EnterEmail.LAST_NAME_KEY, lastName
                )
                putBoolean(
                    RegistrationDestination.EnterEmail.IS_UNVERIFIED_EMAIL_CHANGED_KEY, false
                )
            }
        )
        composeRouter.navigateTo(RegistrationDestination.EnterEmail)
    }

    override fun onChangeEmail() {
        _argsFlow.tryEmit(
            value = RegistrationDestination.EnterEmail to Bundle().apply {
                val (firstName, lastName) = _argsFlow.replayCache.firstOrNull().run {
                    if (this == null)
                        return@run "" to ""

                    val tempFirstName = second.getString(
                        RegistrationDestination.EnterEmail.FIRST_NAME_KEY, ""
                    )
                    val tempLastName = second.getString(
                        RegistrationDestination.EnterEmail.LAST_NAME_KEY, ""
                    )

                    return@run tempFirstName to tempLastName
                }

                putString(
                    RegistrationDestination.EnterEmail.FIRST_NAME_KEY, firstName
                )
                putString(
                    RegistrationDestination.EnterEmail.LAST_NAME_KEY, lastName
                )
                putBoolean(
                    RegistrationDestination.EnterEmail.IS_UNVERIFIED_EMAIL_CHANGED_KEY, true
                )
            }
        )
        composeRouter.navigateTo(RegistrationDestination.EnterEmail)
    }
}