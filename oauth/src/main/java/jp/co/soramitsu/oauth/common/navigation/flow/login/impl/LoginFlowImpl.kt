package jp.co.soramitsu.oauth.common.navigation.flow.login.impl

import android.os.Bundle
import androidx.core.os.bundleOf
import jp.co.soramitsu.oauth.core.engines.activityresult.api.SoraCardResult
import jp.co.soramitsu.oauth.common.navigation.flow.login.api.LoginDestination
import jp.co.soramitsu.oauth.common.navigation.flow.login.api.LoginFlow
import jp.co.soramitsu.oauth.common.navigation.flow.registration.api.RegistrationDestination
import jp.co.soramitsu.oauth.core.engines.activityresult.api.ActivityResult
import jp.co.soramitsu.oauth.core.engines.router.api.ComposeRouter
import jp.co.soramitsu.oauth.core.engines.router.api.SoraCardDestinations
import javax.inject.Inject

class LoginFlowImpl @Inject constructor(
    private val composeRouter: ComposeRouter,
    private val activityResult: ActivityResult
): LoginFlow {

    private val _args = mutableMapOf<String, Bundle>()

    override val args: Map<String, Bundle> = _args

    override fun onStart(destination: LoginDestination) =
        when(destination) {
            is LoginDestination.TermsAndConditions ->
                composeRouter.setNewStartDestination(destination)
            is LoginDestination.EnterPhone ->
                composeRouter.setNewStartDestination(destination)
            is LoginDestination.EnterOtp -> {
                _args[destination::class.java.name] = bundleOf().apply {
                    putInt(
                        LoginDestination.EnterOtp.OTP_LENGTH_KEY,
                        destination.otpLength
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

    override fun onGeneralTermsClicked() {
        // TODO open WebView
    }

    override fun onPrivacyPolicyClicked() {
        // TODO open WebView
    }

    override fun onAcceptTermsAndConditions() {
        composeRouter.navigateTo(LoginDestination.EnterPhone)
    }
}