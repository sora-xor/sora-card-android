package jp.co.soramitsu.oauth.feature.countrylist

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
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.model.countryDialList
import jp.co.soramitsu.oauth.domain.MainCoroutineRule
import jp.co.soramitsu.oauth.feature.verify.phone.CountryListMode
import jp.co.soramitsu.oauth.feature.verify.phone.CountryListState
import jp.co.soramitsu.oauth.feature.verify.phone.CountryListViewModel
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
class CountryListVIewModelTest {

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
    private lateinit var kycRepo: KycRepository

    private lateinit var vm: CountryListViewModel

    @Before
    fun setUp() {
        coEvery { kycRepo.getCountries() } returns countryDialList
        every { mainRouter.backWithCountries(any()) } just runs
        every { mainRouter.back() } just runs
    }

    private fun TestScope.setupViewModel(single: Boolean) {
        vm = CountryListViewModel(
            savedStateHandle = SavedStateHandle(initialState = mapOf(Argument.ADDITIONAL_DESCRIPTION.arg to single)),
            kycRepository = kycRepo,
            mainRouter = mainRouter,
        )
        advanceUntilIdle()
    }

    @Test
    fun `test def toolbar state multi`() = runTest {
        setupViewModel(false)
        val t = vm.toolbarState.getOrAwaitValue()
        assertTrue(t.type is SoramitsuToolbarType.Small)
        assertNull(t.basic.titleArgs)
    }

    @Test
    fun `test toolbar nav`() = runTest {
        setupViewModel(false)
        vm.onToolbarNavigation()
        verify { mainRouter.back() }
    }

    @Test
    fun `test toolbar search`() = runTest {
        setupViewModel(false)
        var s = vm.state.value
        assertEquals(false, s.loading)
        assertEquals(5, s.list.size)
        assertTrue(s.countryListMode is CountryListMode.MultiChoice)
        vm.onToolbarSearch("rus")
        s = vm.state.value
        assertEquals(1, s.list.size)
    }

    @Test
    fun `test def state multi`() = runTest {
        setupViewModel(false)
        val s: CountryListState = vm.state.value
        assertEquals(false, s.loading)
        assertEquals(5, s.list.size)
        assertTrue(s.countryListMode is CountryListMode.MultiChoice)
    }

    @Test
    fun `test multi select`() = runTest {
        setupViewModel(false)
        vm.onSelect(1)
        advanceUntilIdle()
        val s = vm.state.value
        assertEquals(false, s.loading)
        assertEquals(5, s.list.size)
        assertTrue(s.countryListMode is CountryListMode.MultiChoice)
        val selected = (s.countryListMode as CountryListMode.MultiChoice).selectedCodes
        assertEquals(1, selected.size)
        assertEquals("BR", selected[0])
        vm.onDone()
        verify { mainRouter.backWithCountries(listOf("BR")) }
    }
}
