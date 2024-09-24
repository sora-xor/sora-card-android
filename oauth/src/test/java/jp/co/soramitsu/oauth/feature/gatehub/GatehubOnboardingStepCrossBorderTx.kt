package jp.co.soramitsu.oauth.feature.gatehub

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import jp.co.soramitsu.androidfoundation.testing.getOrAwaitValue
import jp.co.soramitsu.oauth.base.navigation.Argument
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.model.countryDialList
import jp.co.soramitsu.oauth.domain.MainCoroutineRule
import jp.co.soramitsu.oauth.feature.gatehub.step2crossbordertx.GatehubCrossBorderTxViewModel
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@OptIn(ExperimentalCoroutinesApi::class)
class GatehubOnboardingStepCrossBorderTx {

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var mainRouter: MainRouter

    @MockK
    private lateinit var inMemoryRepo: InMemoryRepo

    @MockK
    private lateinit var kycRepo: KycRepository

    private lateinit var vm: GatehubCrossBorderTxViewModel

    @Before
    fun setUp() {
        every { inMemoryRepo.ghCountriesFrom = any() } just runs
        every { inMemoryRepo.ghCountriesTo = any() } just runs
        every { mainRouter.back() } just runs
        every { mainRouter.openCountryList(false) } just runs
        every { mainRouter.openGatehubOnboardingStepCrossBorderTx(any()) } just runs
        coEvery { kycRepo.getCountries() } returns countryDialList
    }

    private fun TestScope.setupViewModel(from: Boolean) {
        vm = GatehubCrossBorderTxViewModel(
            mainRouter = mainRouter,
            inMemoryRepo = inMemoryRepo,
            kycRepository = kycRepo,
            savedStateHandle = SavedStateHandle(initialState = mapOf(Argument.ADDITIONAL_DESCRIPTION.arg to from)),
        )
        advanceUntilIdle()
    }

    @Test
    fun `test def toolbar state`() = runTest {
        setupViewModel(true)
        val t = vm.toolbarState.getOrAwaitValue()
        assertTrue(t.type is SoramitsuToolbarType.Small)
        assertNull(t.basic.titleArgs)
    }

    @Test
    fun `test toolbar navigation`() = runTest {
        setupViewModel(true)
        vm.onToolbarNavigation()
        verify { mainRouter.back() }
    }

    @Test
    fun `def state`() = runTest {
        setupViewModel(true)
        val s = vm.state.value
        assertTrue(s.countriesFrom)
        assertEquals(0, s.countries.size)
    }

    @Test
    fun `test on done click`() = runTest {
        setupViewModel(true)
        vm.onDone()
        verify { inMemoryRepo.ghCountriesFrom = emptyList() }
        verify { mainRouter.openGatehubOnboardingStepCrossBorderTx(false) }
    }

    @Test
    fun `test on add click`() = runTest {
        setupViewModel(true)
        vm.onAddCountry()
        verify { mainRouter.openCountryList(false) }
    }

    @Test
    fun `test on remove click`() = runTest {
        setupViewModel(true)
        vm.onRemoveCountry("")
        advanceUntilIdle()
        val s = vm.state.value
        assertEquals(0, s.countries.size)
    }

    @Test
    fun `test set countries`() = runTest {
        setupViewModel(true)
        vm.setCountries(listOf("RU", "BR"))
        advanceUntilIdle()
        val s = vm.state.value
        assertEquals(2, s.countries.size)
    }
}
