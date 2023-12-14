package jp.co.soramitsu.oauth.common.navigation.flow.impl.kycrequiremetsunfulflled

import java.util.Stack
import javax.inject.Inject
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.common.navigation.flow.api.NavigationFlow
import jp.co.soramitsu.oauth.common.navigation.flow.api.destinations.KycRequirementsUnfulfilledDestination
import jp.co.soramitsu.oauth.common.navigation.flow.api.destinations.NavigationFlowDestination

class KycRequirementsUnfulfilledFlowImpl @Inject constructor(
    private val mainRouter: MainRouter,
) : NavigationFlow {

    private val screensStack: Stack<NavigationFlowDestination> =
        Stack<NavigationFlowDestination>()

    override fun start(fromDestination: NavigationFlowDestination) {
        screensStack.apply {
            clear()
            push(fromDestination)
            push(KycRequirementsUnfulfilledDestination.CardIssuanceOptionsScreen())
        }
        mainRouter.navigate(destinationRoute = screensStack.last().destination)
    }

    override fun proceed() {
        when (screensStack.last()) {
            is KycRequirementsUnfulfilledDestination.CardIssuanceOptionsScreen -> {
                screensStack.push(KycRequirementsUnfulfilledDestination.GetMoreXorDialog())
                mainRouter.navigate(destinationRoute = screensStack.last().destination)
            }
            is KycRequirementsUnfulfilledDestination.GetMoreXorDialog -> {
                exit()
            }
            else -> { /* DO NOTHING */ }
        }
    }

    override fun back() {
        when (screensStack.last()) {
            is KycRequirementsUnfulfilledDestination.CardIssuanceOptionsScreen -> {
                exit()
            }
            is KycRequirementsUnfulfilledDestination.GetMoreXorDialog -> {
                screensStack.pop()
                mainRouter.popUpTo(destinationRoute = screensStack.last().destination)
            }
            else -> { /* DO NOTHING */ }
        }
    }

    override fun exit() {
        val root = screensStack[0]
        screensStack.clear()
        mainRouter.popUpTo(destinationRoute = root.destination)
    }
}
