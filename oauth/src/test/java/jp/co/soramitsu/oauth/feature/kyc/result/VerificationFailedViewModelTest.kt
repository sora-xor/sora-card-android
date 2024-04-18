package jp.co.soramitsu.oauth.feature.kyc.result

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.navigation.SetActivityResult
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.domain.MainCoroutineRule
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@OptIn(ExperimentalCoroutinesApi::class)
class VerificationFailedViewModelTest {

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var setActivityResult: SetActivityResult

    @MockK
    private lateinit var pwoAuthClientProxy: PWOAuthClientProxy

    @MockK
    private lateinit var userSessionRepository: UserSessionRepository

    private lateinit var viewModel: VerificationFailedViewModel

    @Before
    fun setUp() {
        every { setActivityResult.setResult(any()) } just runs
        coEvery { pwoAuthClientProxy.logout() } just runs
        coEvery { userSessionRepository.logOutUser() } just runs
        coEvery { userSessionRepository.getPhoneNumber() } returns "+909"
        viewModel = VerificationFailedViewModel(
            setActivityResult = setActivityResult,
            userSessionRepository = userSessionRepository,
            pwoAuthClientProxy = pwoAuthClientProxy,
        )
    }

    @Test
    fun `get phone number`() = runTest {
        advanceUntilIdle()
        assertEquals("+909", viewModel.state.value)
    }

    @Test
    fun `toolbar action`() = runTest {
        advanceUntilIdle()
        viewModel.onToolbarAction()
        advanceUntilIdle()
        coVerify { pwoAuthClientProxy.logout() }
        coVerify { userSessionRepository.logOutUser() }
        verify { setActivityResult.setResult(SoraCardResult.Logout) }
    }

    @Test
    fun `toolbar navigation`() = runTest {
        advanceUntilIdle()
        viewModel.onToolbarNavigation()
        verify {
            setActivityResult.setResult(
                SoraCardResult.Failure(SoraCardCommonVerification.Failed),
            )
        }
    }

    @Test
    fun `init EXPECT toolbar title`() {
        assertEquals(R.string.verification_failed_title, viewModel.toolbarState.value?.basic?.title)
        assertNotNull(viewModel.toolbarState.value?.basic?.navIcon)
    }

    @Test
    fun `call onClose EXPECT finish kyc`() {
        viewModel.onClose()
        verify {
            setActivityResult.setResult(
                SoraCardResult.Failure(SoraCardCommonVerification.Failed),
            )
        }
    }
}
