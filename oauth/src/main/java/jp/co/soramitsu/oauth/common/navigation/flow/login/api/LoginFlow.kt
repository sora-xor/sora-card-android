package jp.co.soramitsu.oauth.common.navigation.flow.login.api

interface LoginFlow {

    fun onStart(destination: LoginDestination)

    fun onBack()

    fun onExit()

    fun onGeneralTermsClicked()

    fun onPrivacyPolicyClicked()

    fun onAcceptTermsAndConditions()

}