package jp.co.soramitsu.oauth.common.navigation.flow.login.impl

import jp.co.soramitsu.oauth.core.engines.activityresult.api.SoraCardResult
import jp.co.soramitsu.oauth.common.navigation.flow.login.api.LoginDestination
import jp.co.soramitsu.oauth.common.navigation.flow.login.api.LoginFlow
import jp.co.soramitsu.oauth.core.engines.activityresult.api.ActivityResult
import jp.co.soramitsu.oauth.core.engines.router.api.ComposeRouter
import jp.co.soramitsu.oauth.core.engines.router.api.SoraCardDestinations
import javax.inject.Inject

class LoginFlowImpl @Inject constructor(
    private val composeRouter: ComposeRouter,
    private val activityResult: ActivityResult
): LoginFlow {

    override fun onStart(destination: LoginDestination) =
        when (destination) {
            is LoginDestination.TermsAndConditions ->
                composeRouter.setNewStartDestination(SoraCardDestinations.TermsAndConditions)
            is LoginDestination.EnterPhone ->
                composeRouter.setNewStartDestination(SoraCardDestinations.EnterPhone)
            is LoginDestination.EnterOtp ->
                composeRouter.setNewStartDestination(
                    SoraCardDestinations.EnterOtp(
                        otpLength = destination.otpLength
                    )
                )
        }.run { composeRouter.clearBackStack() }

    override fun onBack() {
        composeRouter.popBack()
    }

    override fun onExit() {
        activityResult.setResult(SoraCardResult.Canceled)
    }

    override fun onGeneralTermsClicked() {
        // TODO open WebView
    }

    override fun onPrivacyPolicyClicked() {
        // TODO open WebView
    }

    override fun onAcceptTermsAndConditions() {
        composeRouter.navigateTo(SoraCardDestinations.EnterPhone)
    }
}