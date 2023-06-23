package jp.co.soramitsu.oauth.common.navigation.flow.login.impl

import jp.co.soramitsu.oauth.common.navigation.activityresult.api.SoraCardResult
import jp.co.soramitsu.oauth.common.navigation.flow.login.api.LoginDestination
import jp.co.soramitsu.oauth.common.navigation.flow.login.api.LoginFlow
import jp.co.soramitsu.oauth.common.navigation.activityresult.api.ActivityResult
import jp.co.soramitsu.oauth.common.navigation.router.api.ComposeRouter
import javax.inject.Inject

class LoginFlowImpl @Inject constructor(
    private val composeRouter: ComposeRouter,
    private val activityResult: ActivityResult
): LoginFlow {

    override fun onStart(destination: LoginDestination) =
        when (destination) {
            is LoginDestination.TermsAndConditions ->
                composeRouter.setNewStartDestination("TERMS_AND_CONDITIONS")
            is LoginDestination.EnterPhone ->
                composeRouter.setNewStartDestination("ENTER_PHONE")
            is LoginDestination.EnterOtp ->
                composeRouter.setNewStartDestination("ENTER_OTP")
        }.run { composeRouter.clearBackStack() }

    override fun onBack() {
        composeRouter.popBack()
    }

    override fun onExit() {
        activityResult.setResult(SoraCardResult.Canceled)
    }

    override fun onGeneralTermsClicked() {
        TODO("Not yet implemented")
    }

    override fun onPrivacyPolicyClicked() {
        // TODO open WebView
    }

    override fun onAcceptTermsAndConditions() {
        composeRouter.navigateTo("ENTER_PHONE")
    }

    override fun onConfirmSendingOtpCode() {
        composeRouter.navigateTo("ENTER_OTP")
    }
}