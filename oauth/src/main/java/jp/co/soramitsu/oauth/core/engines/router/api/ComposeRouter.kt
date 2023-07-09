package jp.co.soramitsu.oauth.core.engines.router.api

import androidx.compose.runtime.State
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.StateFlow

interface ComposeRouter {

    val startDestination: StateFlow<SoraCardDestinations>

    val navController: NavHostController

    fun setNewStartDestination(destination: SoraCardDestinations)

    fun navigateTo(destination: SoraCardDestinations)

    fun popBack()

    fun clearBackStack()
}