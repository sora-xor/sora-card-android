package jp.co.soramitsu.oauth.common.navigation.flow.registration.api

import android.os.Bundle
import jp.co.soramitsu.oauth.core.engines.router.api.SoraCardDestinations
import kotlinx.coroutines.flow.SharedFlow

interface RegistrationFlow {

    val argsFlow: SharedFlow<Pair<SoraCardDestinations, Bundle>>

    fun onStart(destination: RegistrationDestination)

    fun onBack()

    fun onExit()

    fun onLogout()

    fun onEnterEmail(firstName: String, lastName: String)

    fun onChangeEmail()

}