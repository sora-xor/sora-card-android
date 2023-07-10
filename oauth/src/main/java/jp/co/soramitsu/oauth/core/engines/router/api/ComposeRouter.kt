package jp.co.soramitsu.oauth.core.engines.router.api

import androidx.compose.runtime.State
import androidx.navigation.NavHostController

interface ComposeRouter {

    val startDestination: State<SoraCardDestinations>

    val navController: NavHostController

    fun setNewStartDestination(destination: SoraCardDestinations)

    fun navigateTo(destination: SoraCardDestinations)

    fun popBack(): Boolean
}