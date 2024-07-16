package jp.co.soramitsu.oauth.feature

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import jp.co.soramitsu.androidfoundation.format.unsafeCast
import jp.co.soramitsu.androidfoundation.testing.getOrAwaitValue
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardFlow
import jp.co.soramitsu.oauth.domain.MainCoroutineRule
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.oauth.feature.usernotfound.UserNotFoundViewModel
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@OptIn(ExperimentalCoroutinesApi::class)
class UserNotFoundViewModelTest {

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
    private lateinit var userSessionRepository: UserSessionRepository

    @MockK
    private lateinit var inMemoryRepo: InMemoryRepo

    private lateinit var vm: UserNotFoundViewModel

    @Before
    fun setUp() {
        coEvery { userSessionRepository.getPhoneNumber() } returns "+1239"
        every { mainRouter.back() } just runs
        every { mainRouter.openEnterPhoneNumber(true) } just runs
        every { inMemoryRepo.flow!!.unsafeCast<SoraCardFlow.SoraCardKycFlow>().logIn = any() } just runs
        vm = UserNotFoundViewModel(
            mainRouter, inMemoryRepo, userSessionRepository,
        )
    }

    @Test
    fun `test toolbar`() {
        val toolbar = vm.toolbarState.getOrAwaitValue()
        assertEquals("", toolbar.basic.title)
        assertTrue(toolbar.type is SoramitsuToolbarType.Small)
    }

    @Test
    fun `test state`() = runTest {
        advanceUntilIdle()
        assertEquals("+1239", vm.state.value)
    }

    @Test
    fun `test nav`() {
        vm.onToolbarNavigation()
        verify { mainRouter.back() }
    }

    @Test
    fun `test another number`() {
        vm.onTryAnotherNumber()
        verify(
            exactly = 0,
        ) { inMemoryRepo.flow!!.unsafeCast<SoraCardFlow.SoraCardKycFlow>().logIn = any() }
        verify { mainRouter.openEnterPhoneNumber(true) }
    }

    @Test
    fun `test reg new`() {
        vm.onRegisterNewAccount()
        verify { inMemoryRepo.flow!!.unsafeCast<SoraCardFlow.SoraCardKycFlow>().logIn = false }
        verify { mainRouter.openEnterPhoneNumber(true) }
    }
}
