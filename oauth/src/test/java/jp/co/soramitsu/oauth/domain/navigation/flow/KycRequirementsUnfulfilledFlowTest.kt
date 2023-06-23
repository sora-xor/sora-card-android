package jp.co.soramitsu.oauth.domain.navigation.flow

import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.common.navigation.flow.api.NavigationFlow
import jp.co.soramitsu.oauth.common.navigation.flow.api.destinations.CompatibilityDestination
import jp.co.soramitsu.oauth.common.navigation.flow.api.destinations.KycRequirementsUnfulfilledFlowDestination
import jp.co.soramitsu.oauth.common.navigation.flow.api.destinations.NavigationFlowDestination
import jp.co.soramitsu.oauth.common.navigation.flow.impl.flows.KycRequirementsUnfulfilledFlowImpl
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class KycRequirementsUnfulfilledFlowTest {

    @Mock
    private lateinit var mainRouter: MainRouter

    private lateinit var startingDestination: NavigationFlowDestination

    private lateinit var kycRequirementsUnfulfilledFlow: NavigationFlow

    @Before
    fun setUp() {
        CompatibilityDestination(
            destination = "Test Destination"
        ).apply { startingDestination = this }

        KycRequirementsUnfulfilledFlowImpl(
            mainRouter = mainRouter
        ).apply { kycRequirementsUnfulfilledFlow = this }
    }

    @Test
    fun `start, proceed, back, proceed, exit flow EXPECT correct navigation`() {
        kycRequirementsUnfulfilledFlow.start(startingDestination)

        KycRequirementsUnfulfilledFlowDestination.CardIssuanceOptionsScreen().destination.let {
            verify(mainRouter).navigate(destinationRoute = it)
        }

        kycRequirementsUnfulfilledFlow.proceed()

        KycRequirementsUnfulfilledFlowDestination.GetMoreXorDialog().destination.let {
            verify(mainRouter).navigate(destinationRoute = it)
        }

        kycRequirementsUnfulfilledFlow.back()

        KycRequirementsUnfulfilledFlowDestination.CardIssuanceOptionsScreen().destination.let {
            verify(mainRouter).popUpTo(destinationRoute = it)
        }

        kycRequirementsUnfulfilledFlow.proceed()

        KycRequirementsUnfulfilledFlowDestination.GetMoreXorDialog().destination.let {
            verify(
                mainRouter,
                times(2)
            ).navigate(destinationRoute = it)
        }

        kycRequirementsUnfulfilledFlow.exit()

        startingDestination.destination.let {
            verify(mainRouter).popUpTo(destinationRoute = it)
        }
    }
}