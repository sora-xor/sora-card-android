package jp.co.soramitsu.oauth.feature.gatehub

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.domain.MainCoroutineRule
import jp.co.soramitsu.oauth.feature.gatehub.step3.GatehubOnboardingStep3ViewModel
import jp.co.soramitsu.oauth.getOrAwaitValue
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@OptIn(ExperimentalCoroutinesApi::class)
class GatehubOnboardingStep3ViewModelTest {

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var mainRouter: MainRouter

    @MockK
    private lateinit var inMemoryRepo: InMemoryRepo

    @MockK
    private lateinit var gateHubRepository: GateHubRepository

    private lateinit var vm: GatehubOnboardingStep3ViewModel

    @Before
    fun setUp() {
        every { mainRouter.back() } just runs
        every { mainRouter.openCountryList() } just runs
        every { inMemoryRepo.ghSourceOfFunds = any() } just runs
        every { mainRouter.openWebUrl(any()) } just runs
        every { mainRouter.openGatehubOnboardingProgress() } just runs
        vm = GatehubOnboardingStep3ViewModel(mainRouter, inMemoryRepo, gateHubRepository)
    }

    @Test
    fun `toolbar check`() {
        val t = vm.toolbarState.getOrAwaitValue()
        assertTrue(t.type is SoramitsuToolbarType.Small)
        assertTrue(t.basic.titleArgs?.size == 2)
        assertEquals(3, t.basic.titleArgs?.get(0))
        assertEquals(3, t.basic.titleArgs?.get(1))
    }

    @Test
    fun `toolbar nav`() = runTest {
        advanceUntilIdle()
        vm.onToolbarNavigation()
        verify { mainRouter.back() }
    }

    @Test
    fun `next step`() = runTest {
        advanceUntilIdle()
        coEvery { gateHubRepository.onboardUser() } returns Result.success(0 to "")
        coEvery { gateHubRepository.getIframe() } returns Result.success(IframeModel(0, "", "iurl"))
        vm.onNext()
        advanceUntilIdle()
        verify { mainRouter.openGatehubOnboardingProgress() }
    }

    @Test
    fun `def state`() = runTest {
        advanceUntilIdle()
        val s = vm.state.value
        assertFalse(s.buttonEnabled)
        assertNull(s.selectedPos)
        assertEquals(4, s.sources.size)
    }

    @Test
    fun `item select`() = runTest {
        advanceUntilIdle()
        val s = vm.state.value
        assertFalse(s.buttonEnabled)
        assertNull(s.selectedPos)
        vm.onItemSelected(2)
        advanceUntilIdle()
        val s2 = vm.state.value
        assertTrue(s2.buttonEnabled)
        assertNotNull(s2.selectedPos)
        assertArrayEquals(arrayOf(2), s2.selectedPos?.toTypedArray())
    }

    @Test
    fun `item select twice`() = runTest {
        advanceUntilIdle()
        val s = vm.state.value
        assertFalse(s.buttonEnabled)
        assertNull(s.selectedPos)
        vm.onItemSelected(0)
        vm.onItemSelected(2)
        advanceUntilIdle()
        val s2 = vm.state.value
        assertTrue(s2.buttonEnabled)
        assertNotNull(s2.selectedPos)
        assertArrayEquals(arrayOf(0, 2), s2.selectedPos?.toTypedArray())
        vm.onItemSelected(2)
        advanceUntilIdle()
        val s3 = vm.state.value
        assertTrue(s3.buttonEnabled)
        assertNotNull(s3.selectedPos)
        assertArrayEquals(arrayOf(0), s3.selectedPos?.toTypedArray())
    }
}
