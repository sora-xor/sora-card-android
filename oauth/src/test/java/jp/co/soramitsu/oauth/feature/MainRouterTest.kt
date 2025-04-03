package jp.co.soramitsu.oauth.feature

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.navigation.MainRouterImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainRouterTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var nav: NavHostController

    @MockK
    private lateinit var activity: Activity

    @MockK
    private lateinit var navEntry: NavBackStackEntry

    @MockK
    private lateinit var ssh: SavedStateHandle

    private lateinit var mr: MainRouter

    @Before
    fun setUp() {
        every { nav.popBackStack() } returns true
        every { nav.navigate(route = any(), builder = any()) } just runs
        every { nav.navigate(route = any(), navOptions = any(), navigatorExtras = any()) } just runs
        every { nav.previousBackStackEntry } returns navEntry
        every { navEntry.savedStateHandle } returns ssh
        every { ssh[any()] = any<String>() } just runs
        every { ssh[any()] = any<List<String>>() } just runs
        mr = MainRouterImpl()
        mr.attachNavController(activity, nav)
    }

    @Test
    fun `test back`() = runTest {
        advanceUntilIdle()
        mr.back()
        verify { nav.popBackStack() }
    }

    @Test
    fun `test gatehub employment status`() = runTest {
        advanceUntilIdle()
        mr.openGatehubOnboardingStepEmploymentStatus()
        verify { nav.navigate("oauth/gatehubOnboardingStepEmploymentStatus") }
    }

    @Test
    fun `test gatehub step1 status`() = runTest {
        advanceUntilIdle()
        mr.openGatehubOnboardingStep1()
        verify { nav.navigate("oauth/gatehubOnboardingStep1") }
    }

    @Test
    fun `test gatehub step2 status`() = runTest {
        advanceUntilIdle()
        mr.openGatehubOnboardingStep2()
        verify { nav.navigate("oauth/gatehubOnboardingStep2") }
    }

    @Test
    fun `test gatehub step3 status`() = runTest {
        advanceUntilIdle()
        mr.openGatehubOnboardingStep3()
        verify { nav.navigate("oauth/gatehubOnboardingStep3") }
    }

    @Test
    fun `test gatehub cross border status`() = runTest {
        advanceUntilIdle()
        mr.openGatehubOnboardingStepCrossBorderTx(false)
        verify { nav.navigate("oauth/gatehubOnboardingStepCrossBorderTx/false") }
    }

    @Test
    fun `test gatehub progress status`() = runTest {
        advanceUntilIdle()
        val slot = slot<NavOptionsBuilder.() -> Unit>()
        mr.openGatehubOnboardingProgress()
        verify { nav.navigate(route = "oauth/gatehubOnboardingProgress", builder = capture(slot)) }
    }

    @Test
    fun `test gatehub rejected status`() = runTest {
        advanceUntilIdle()
        val slot = slot<NavOptionsBuilder.() -> Unit>()
        mr.openGatehubOnboardingRejected("reason")
        verify {
            nav.navigate(
                route = "oauth/gatehubOnboardingRejected/reason",
                builder = capture(slot),
            )
        }
    }

    @Test
    fun `test gatehub get prepared status`() = runTest {
        advanceUntilIdle()
        val slot = slot<NavOptionsBuilder.() -> Unit>()
        mr.openGetPrepared()
        verify { nav.navigate(route = "oauth/getPrepared", builder = capture(slot)) }
    }

    @Test
    fun `test country list`() = runTest {
        advanceUntilIdle()
        mr.openCountryList(false)
        verify { nav.navigate("oauth/selectCountry/false") }
    }

    @Test
    fun `test back with country`() = runTest {
        advanceUntilIdle()
        mr.backWithCountry("x0y")
        verify { nav.popBackStack() }
        verify { ssh["country_code"] = "x0y" }
    }

    @Test
    fun `test back with countries`() = runTest {
        advanceUntilIdle()
        val l = listOf("x0y", "y4m")
        mr.backWithCountries(l)
        verify { nav.popBackStack() }
        verify { ssh["country_code"] = l }
    }

    @Test
    fun `test enter phone`() = runTest {
        advanceUntilIdle()
        val slot = slot<NavOptionsBuilder.() -> Unit>()
        mr.openEnterPhoneNumber(false)
        verify { nav.navigate(route = "oauth/enterPhoneNumber", builder = capture(slot)) }
    }

    @Test
    fun `test terms and`() = runTest {
        advanceUntilIdle()
        val slot = slot<NavOptionsBuilder.() -> Unit>()
        mr.openTermsAndConditions()
        verify { nav.navigate(route = "oauth/termsAndConditions", builder = capture(slot)) }
    }

    @Test
    fun `test reg user`() = runTest {
        advanceUntilIdle()
        mr.openRegisterUser()
        verify { nav.navigate("oauth/registerUser") }
    }

    @Test
    fun `test user not found`() = runTest {
        advanceUntilIdle()
        mr.openLogInFailedUserNotFound()
        verify { nav.navigate("oauth/userNotFound") }
    }

    @Test
    fun `test enter email`() = runTest {
        advanceUntilIdle()
        mr.openEnterEmail("f", "l")
        verify { nav.navigate("oauth/enterEmail/f/l") }
    }

    @Test
    fun `test verify email`() = runTest {
        advanceUntilIdle()
        val slot = slot<NavOptionsBuilder.() -> Unit>()
        mr.openVerifyEmail(email = "em", autoEmailSent = false, clearStack = false)
        verify { nav.navigate(route = "oauth/verifyEmail/em/false", builder = capture(slot)) }
    }
}
