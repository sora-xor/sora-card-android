package jp.co.soramitsu.oauth.common.navigation.flow.login.impl

import android.os.Bundle
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.core.engines.activityresult.api.SoraCardResult
import jp.co.soramitsu.oauth.common.navigation.flow.login.api.LoginDestination
import jp.co.soramitsu.oauth.common.navigation.flow.login.api.LoginFlow
import jp.co.soramitsu.oauth.core.engines.activityresult.api.ActivityResult
import jp.co.soramitsu.oauth.core.engines.router.api.ComposeRouter
import jp.co.soramitsu.oauth.core.engines.router.api.SoraCardDestinations
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class LoginFlowImpl @Inject constructor(
    private val composeRouter: ComposeRouter,
    private val activityResult: ActivityResult
): LoginFlow {

    private val _argsFlow = MutableSharedFlow<Pair<SoraCardDestinations, Bundle>>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val argsFlow: SharedFlow<Pair<SoraCardDestinations, Bundle>> = _argsFlow

    override fun onStart(destination: LoginDestination) =
        when(destination) {
            is LoginDestination.EnterOtp -> {
                _argsFlow.tryEmit(
                    value = destination to Bundle().apply {
                        putInt(
                            LoginDestination.EnterOtp.OTP_LENGTH_KEY,
                            destination.otpLength
                        )
                    }
                )
                composeRouter.navigateTo(destination)
            }
            else -> composeRouter.setNewStartDestination(destination)
        }

    override fun onBack() {
        val isSuccessful = composeRouter.popBack()

        if (!isSuccessful)
            activityResult.setResult(SoraCardResult.Canceled)
    }

    override fun onExit() {
        activityResult.setResult(SoraCardResult.Canceled)
    }

    override fun onGeneralTermsClicked() {
        val title = R.string.terms_and_conditions_general_terms
        val url = "https://soracard.com/terms/"

        _argsFlow.tryEmit(
            value = LoginDestination.WebPage to Bundle().apply {
                putInt(LoginDestination.WebPage.TITLE_STRING_RES_KEY, title)
                putString(LoginDestination.WebPage.URL_KEY, url)
            }
        )
        composeRouter.navigateTo(LoginDestination.WebPage)
    }

    override fun onPrivacyPolicyClicked() {
        val title = R.string.terms_and_conditions_privacy_policy
        val url = "https://soracard.com/privacy/"

        _argsFlow.tryEmit(
            value = LoginDestination.WebPage to Bundle().apply {
                putInt(LoginDestination.WebPage.TITLE_STRING_RES_KEY, title)
                putString(LoginDestination.WebPage.URL_KEY, url)
            }
        )
        composeRouter.navigateTo(LoginDestination.WebPage)
    }

    override fun onAcceptTermsAndConditions() {
        composeRouter.navigateTo(LoginDestination.EnterPhone)
    }
}