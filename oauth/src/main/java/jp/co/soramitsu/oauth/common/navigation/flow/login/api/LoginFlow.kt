package jp.co.soramitsu.oauth.common.navigation.flow.login.api

import android.os.Bundle

interface LoginFlow {

    val args: Map<String, Bundle>

    fun onStart(destination: LoginDestination)

    fun onBack()

    fun onExit()

    fun onGeneralTermsClicked()

    fun onPrivacyPolicyClicked()

    fun onAcceptTermsAndConditions()

}