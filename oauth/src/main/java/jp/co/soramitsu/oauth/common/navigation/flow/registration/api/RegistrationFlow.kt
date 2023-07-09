package jp.co.soramitsu.oauth.common.navigation.flow.registration.api

import android.os.Bundle

interface RegistrationFlow {

    val args: Map<String, Bundle>

    fun onStart(destination: RegistrationDestination)

    fun onBack()

    fun onExit()

    fun onEnterEmail(firstName: String, lastName: String)

}