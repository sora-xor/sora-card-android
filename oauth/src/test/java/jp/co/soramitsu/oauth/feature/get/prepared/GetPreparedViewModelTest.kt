package jp.co.soramitsu.oauth.feature.get.prepared

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.navigation.SetActivityResult
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.common.domain.PriceInteractor
import jp.co.soramitsu.oauth.common.model.KycAttemptsDto
import jp.co.soramitsu.oauth.domain.MainCoroutineRule
import jp.co.soramitsu.oauth.feature.OAuthCallback
import jp.co.soramitsu.oauth.feature.getprepared.GetPreparedViewModel
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@OptIn(ExperimentalCoroutinesApi::class)
class GetPreparedViewModelTest {

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var userSessionRepository: UserSessionRepository

    @MockK
    private lateinit var setActivityResult: SetActivityResult

    @MockK
    private lateinit var authCallback: OAuthCallback

    @MockK
    private lateinit var kyc: KycRepository

    @MockK
    private lateinit var pwoAuthClientProxy: PWOAuthClientProxy

    @MockK
    private lateinit var interactor: PriceInteractor

    private lateinit var viewModel: GetPreparedViewModel

    @Before
    fun setUp() {
        every { setActivityResult.setResult(any()) } returns Unit
        every { authCallback.onStartKyc() } returns Unit
        coEvery { userSessionRepository.getPhoneNumber() } returns "+987"
        coEvery { userSessionRepository.getAccessToken() } returns "token"
        coEvery { kyc.getFreeKycAttemptsInfo("token") } returns Result.success(
            KycAttemptsDto(
                total = 4,
                completed = 1,
                rejected = 0,
                freeAttemptAvailable = true,
                freeAttemptsCount = 2,
                totalFreeAttemptsCount = 3,
                successful = 0,
                retry = 1,
            ),
        )
        coEvery { interactor.calculateKycAttemptPrice() } returns ""
        viewModel = GetPreparedViewModel(
            setActivityResult,
            userSessionRepository,
            kyc,
            interactor,
            pwoAuthClientProxy,
        )
    }

    @Test
    fun `init EXPECT toolbar title`() {
        assertEquals(R.string.get_prepared_title, viewModel.toolbarState.value?.basic?.title)
    }

    @Test
    fun `init EXPECT set steps`() = runTest {
        advanceUntilIdle()
        assertEquals(TestData.STEPS, viewModel.state.value.steps)
    }

    @Test
    fun `on confirm EXPECT start kyc`() {
        viewModel.setArgs(authCallback)
        viewModel.onConfirm()
        verify { authCallback.onStartKyc() }
    }

    @Test
    fun `back EXPECT navigate back`() {
        viewModel.onToolbarNavigation()
        verify { setActivityResult.setResult(any()) }
    }
}
