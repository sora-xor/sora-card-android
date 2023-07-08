package jp.co.soramitsu.oauth.core.engines.router.impl

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavHostController
import jp.co.soramitsu.oauth.core.engines.router.api.ComposeRouter
import jp.co.soramitsu.oauth.core.engines.router.api.SoraCardDestinations
import javax.inject.Inject

class ComposeRouterImpl @Inject constructor(
    private val navHostController: NavHostController
): ComposeRouter {

    private val _startDestinationState = mutableStateOf<SoraCardDestinations>(SoraCardDestinations.Loading)

    override val startDestination: State<SoraCardDestinations> = _startDestinationState

    override val navController: NavHostController = navHostController

    override fun setNewStartDestination(destination: SoraCardDestinations) {
//        _startDestinationState.value = destination
    }

    override fun navigateTo(destination: SoraCardDestinations) {
//        navHostController.navigate(destination.route)
    }

    override fun popBack() {
//        navHostController.popBackStack()
    }

    override fun clearBackStack() {
//        navHostController.popBackStack(navHostController.graph.id, true)
    }
}