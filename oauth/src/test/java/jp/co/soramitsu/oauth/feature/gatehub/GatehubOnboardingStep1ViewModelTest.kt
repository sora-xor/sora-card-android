package jp.co.soramitsu.oauth.feature.gatehub

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.navigation.SetActivityResult
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.domain.MainCoroutineRule
import jp.co.soramitsu.oauth.getOrAwaitValue
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
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
class GatehubOnboardingStep1ViewModelTest {

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
    private lateinit var setActivityResult: SetActivityResult

    private lateinit var vm: GatehubOnboardingStep1ViewModel

    @Before
    fun setUp() {
        every { mainRouter.openGatehubOnboardingStep2() } just runs
        every { setActivityResult.setResult(any()) } just runs
        every { inMemoryRepo.ghExpectedExchangeVolume = any() } just runs
        vm = GatehubOnboardingStep1ViewModel(mainRouter, setActivityResult, inMemoryRepo)
    }

    @Test
    fun `toolbar check`() {
        val t = vm.toolbarState.getOrAwaitValue()
        assertTrue(t.type is SoramitsuToolbarType.Small)
        assertTrue(t.basic.titleArgs?.size == 2)
    }

    @Test
    fun `toolbar nav`() = runTest {
        advanceUntilIdle()
        vm.onToolbarNavigation()
        verify { setActivityResult.setResult(SoraCardResult.Canceled) }
    }

    @Test
    fun `def state`() = runTest {
        advanceUntilIdle()
        val s = vm.state.value
        assertFalse(s.buttonEnabled)
        assertNull(s.selectedPos)
        assertEquals(5, s.amounts.size)
    }

    @Test
    fun `select item`() = runTest {
        advanceUntilIdle()
        val s = vm.state.value
        assertFalse(s.buttonEnabled)
        assertNull(s.selectedPos)
        vm.onItemSelected(1)
        advanceUntilIdle()
        val s2 = vm.state.value
        assertTrue(s2.buttonEnabled)
        assertNotNull(s2.selectedPos)
        assertEquals(1, s2.selectedPos)
    }

    @Test
    fun `next click`() = runTest {
        advanceUntilIdle()
        vm.onNext()
        verify { mainRouter.openGatehubOnboardingStep2() }
    }
}
