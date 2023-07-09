package jp.co.soramitsu.oauth.common.navigation.flow.login.impl

import android.os.Bundle
import androidx.core.os.bundleOf
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.core.engines.activityresult.api.SoraCardResult
import jp.co.soramitsu.oauth.common.navigation.flow.login.api.LoginDestination
import jp.co.soramitsu.oauth.common.navigation.flow.login.api.LoginFlow
import jp.co.soramitsu.oauth.core.engines.activityresult.api.ActivityResult
import jp.co.soramitsu.oauth.core.engines.router.api.ComposeRouter
import javax.inject.Inject

class LoginFlowImpl @Inject constructor(
    private val composeRouter: ComposeRouter,
    private val activityResult: ActivityResult
): LoginFlow {

    private val _args = mutableMapOf<String, Bundle>()

    override val args: Map<String, Bundle> = _args

    override fun onStart(destination: LoginDestination) =
        when(destination) {
            is LoginDestination.EnterOtp -> {
                _args[destination::class.java.name] = bundleOf().apply {
                    putInt(
                        LoginDestination.EnterOtp.OTP_LENGTH_KEY,
                        destination.otpLength
                    )
                }
                composeRouter.setNewStartDestination(destination)
            }
            else -> composeRouter.setNewStartDestination(destination)
        }

    override fun onBack() {
        composeRouter.popBack()
    }

    override fun onExit() {
        activityResult.setResult(SoraCardResult.Canceled)
    }

    override fun onGeneralTermsClicked() {
        val title = R.string.terms_and_conditions_general_terms
        val url = "https://soracard.com/terms/"

        _args[LoginDestination.WebPage::class.java.name] = Bundle().apply {
            putInt(LoginDestination.WebPage.TITLE_STRING_RES_KEY, title)
            putString(LoginDestination.WebPage.URL_KEY, url)
        }
        composeRouter.navigateTo(LoginDestination.WebPage)
    }

    override fun onPrivacyPolicyClicked() {
        val title = R.string.terms_and_conditions_privacy_policy
        val url = "https://soracard.com/privacy/"

        _args[LoginDestination.WebPage::class.java.name] = Bundle().apply {
            putInt(LoginDestination.WebPage.TITLE_STRING_RES_KEY, title)
            putString(LoginDestination.WebPage.URL_KEY, url)
        }
        composeRouter.navigateTo(LoginDestination.WebPage)
    }

    override fun onAcceptTermsAndConditions() {
        composeRouter.navigateTo(LoginDestination.EnterPhone)
    }
}