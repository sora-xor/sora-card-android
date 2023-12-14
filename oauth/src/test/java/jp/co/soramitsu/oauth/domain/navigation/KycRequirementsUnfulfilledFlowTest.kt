package jp.co.soramitsu.oauth.domain.navigation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.common.navigation.flow.api.NavigationFlow
import jp.co.soramitsu.oauth.common.navigation.flow.api.destinations.CompatibilityDestination
import jp.co.soramitsu.oauth.common.navigation.flow.api.destinations.KycRequirementsUnfulfilledDestination
import jp.co.soramitsu.oauth.common.navigation.flow.api.destinations.NavigationFlowDestination
import jp.co.soramitsu.oauth.common.navigation.flow.impl.kycrequiremetsunfulflled.KycRequirementsUnfulfilledFlowImpl
import jp.co.soramitsu.oauth.domain.MainCoroutineRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class KycRequirementsUnfulfilledFlowTest {

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var mainRouter: MainRouter

    private lateinit var startingDestination: NavigationFlowDestination

    private lateinit var kycRequirementsUnfulfilledFlow: NavigationFlow

    @Before
    fun setUp() {
        every { mainRouter.navigate(any()) } returns Unit
        every { mainRouter.popUpTo(any()) } returns Unit
        startingDestination = CompatibilityDestination(
            destination = "Test Destination",
        )

        kycRequirementsUnfulfilledFlow = KycRequirementsUnfulfilledFlowImpl(
            mainRouter = mainRouter,
        )
    }

    @Test
    fun `start, proceed, back, proceed, exit flow EXPECT correct navigation`() {
        kycRequirementsUnfulfilledFlow.start(startingDestination)

        KycRequirementsUnfulfilledDestination.CardIssuanceOptionsScreen().destination.let {
            verify { mainRouter.navigate(it) }
        }

        kycRequirementsUnfulfilledFlow.proceed()

        KycRequirementsUnfulfilledDestination.GetMoreXorDialog().destination.let {
            verify { mainRouter.navigate(it) }
        }

        kycRequirementsUnfulfilledFlow.back()

        KycRequirementsUnfulfilledDestination.CardIssuanceOptionsScreen().destination.let {
            verify { mainRouter.popUpTo(it) }
        }

        kycRequirementsUnfulfilledFlow.proceed()

        KycRequirementsUnfulfilledDestination.GetMoreXorDialog().destination.let {
            verify(exactly = 2) { mainRouter.navigate(it) }
        }

        kycRequirementsUnfulfilledFlow.exit()

        startingDestination.destination.let {
            verify { mainRouter.popUpTo(it) }
        }
    }
}
