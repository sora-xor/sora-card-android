package jp.co.soramitsu.oauth.feature

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.test.MainCoroutineRule
import jp.co.soramitsu.oauth.core.datasources.tachi.api.KycRepository
import jp.co.soramitsu.oauth.common.navigation.flow.api.NavigationFlow
import jp.co.soramitsu.oauth.core.datasources.session.api.UserSessionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var userSessionRepository: UserSessionRepository

    @Mock
    private lateinit var kycRepository: KycRepository

    @Mock
    private lateinit var mainRouter: MainRouter

    @Mock
    private lateinit var kycRequirementsUnfulfilledFlow: NavigationFlow

    @Mock
    private lateinit var inMemoryRepo: InMemoryRepo

    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() = runTest {
        viewModel = MainViewModel(
            userSessionRepository,
            kycRepository,
            mainRouter,
            inMemoryRepo,
            kycRequirementsUnfulfilledFlow
        )
    }

    @Test
    fun `no free kyc tries EXPECT show kyc requirements unfulfilled flow started`() = runTest {
        given(kycRepository.hasFreeKycAttempt("accessToken")).willReturn(Result.success(false))

        viewModel.onAuthSucceed("accessToken")
        advanceUntilIdle()

        verify(kycRequirementsUnfulfilledFlow).start(any())
    }

    @Test
    fun `free kyc tries available EXPECT show get prepared screen`() = runTest {
        given(kycRepository.hasFreeKycAttempt("accessToken")).willReturn(Result.success(true))

        viewModel.onAuthSucceed("accessToken")
        advanceUntilIdle()

        verify(mainRouter).openGetPrepared()
    }

    @Test
    fun `on onKycFailed EXPECT navigate to verification failed screen`() = runTest {
        val description = "description"
        viewModel.onKycFailed(statusDescription = description)
        advanceUntilIdle()

        verify(mainRouter).openVerificationFailed(additionalDescription = description)
    }
}