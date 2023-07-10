package jp.co.soramitsu.oauth.common.navigation.flow.login.api

import android.os.Bundle
import jp.co.soramitsu.oauth.core.engines.router.api.SoraCardDestinations
import kotlinx.coroutines.flow.SharedFlow

interface LoginFlow {

    val argsFlow: SharedFlow<Pair<SoraCardDestinations, Bundle>>

    fun onStart(destination: LoginDestination)

    fun onBack()

    fun onExit()

    fun onGeneralTermsClicked()

    fun onPrivacyPolicyClicked()

    fun onAcceptTermsAndConditions()

}