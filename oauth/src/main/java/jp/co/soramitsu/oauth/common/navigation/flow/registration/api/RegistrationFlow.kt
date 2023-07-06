package jp.co.soramitsu.oauth.common.navigation.flow.registration.api

interface RegistrationFlow {

    fun onStart(destination: RegistrationDestination)

    fun onBack()

    fun onExit()

    fun onEnterEmail(firstName: String, lastName: String)

}