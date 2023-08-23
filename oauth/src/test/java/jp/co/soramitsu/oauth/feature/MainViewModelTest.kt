package jp.co.soramitsu.oauth.feature

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.paywings.oauth.android.sdk.service.callback.GetUserDataCallback
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.slot
import io.mockk.verify
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.base.test.MainCoroutineRule
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.common.model.AccessTokenResponse
import jp.co.soramitsu.oauth.common.navigation.flow.api.NavigationFlow
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

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
    private lateinit var kycRepository: KycRepository

    @MockK
    private lateinit var mainRouter: MainRouter

    @MockK
    private lateinit var tokenValidator: AccessTokenValidator

    @MockK
    private lateinit var kycRequirementsUnfulfilledFlow: NavigationFlow

    @MockK
    private lateinit var pwoAuthClientProxy: PWOAuthClientProxy

    @MockK
    private lateinit var inMemoryRepo: InMemoryRepo

    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() = runTest {
        coEvery { userSessionRepository.getAccessToken() } returns "accessToken"
        coEvery { userSessionRepository.getAccessTokenExpirationTime() } returns System.currentTimeMillis() + 300000
        coEvery { userSessionRepository.setNewAccessToken(any(), any()) } returns Unit
        coEvery { userSessionRepository.getUser() } returns Triple("refresh", "access", 0)
        coEvery { kycRequirementsUnfulfilledFlow.start(any()) } returns Unit
        every { mainRouter.openGetPrepared() } returns Unit
        every { mainRouter.openVerificationFailed(any()) } returns Unit
    }

    private fun setupViewModel(status: SoraCardCommonVerification) {
        coEvery { kycRepository.getKycLastFinalStatus("accessToken") } returns Result.success(status)
        viewModel = MainViewModel(
            userSessionRepository,
            kycRepository,
            mainRouter,
            inMemoryRepo,
            pwoAuthClientProxy,
            kycRequirementsUnfulfilledFlow,
            tokenValidator,
        )
    }

    @Test
    fun `authCallback getUserData`() = runTest {
        every { inMemoryRepo.isEnoughXorAvailable } returns true
        coEvery { kycRepository.hasFreeKycAttempt("accessToken") } returns Result.success(true)
        coEvery { userSessionRepository.getRefreshToken() } returns "refreshToken"
        coEvery { tokenValidator.checkAccessTokenValidity() } returns AccessTokenResponse.Token("token", 1000)
        val slot = slot<GetUserDataCallback>()
        coEvery { pwoAuthClientProxy.getUserData(any(), capture(slot)) } answers {
            slot.captured.onUserData("", "", "", "", false, "")
        }
        coEvery { userSessionRepository.setUserId(any()) } returns Unit
        coEvery { kycRepository.getReferenceNumber(any(), any(), any()) } returns Result.success("refnumber")
        setupViewModel(SoraCardCommonVerification.Failed)
        advanceUntilIdle()
        viewModel.getUserData()
        advanceUntilIdle()
        val refNumber = viewModel.state.value?.referenceNumber
        assertTrue(refNumber != null)
    }

    @Test
    fun `no free kyc tries EXPECT show kyc requirements unfulfilled flow started`() = runTest {
        every { inMemoryRepo.isEnoughXorAvailable } returns false
        coEvery { kycRepository.hasFreeKycAttempt("accessToken") } returns Result.success(true)
        setupViewModel(SoraCardCommonVerification.Failed)
        viewModel.onAuthSucceed("accessToken")
        advanceUntilIdle()
        verify { kycRequirementsUnfulfilledFlow.start(any()) }
    }

    @Test
    fun `free kyc tries available EXPECT show get prepared screen`() = runTest {
        every { inMemoryRepo.isEnoughXorAvailable } returns true
        coEvery { kycRepository.hasFreeKycAttempt("accessToken") } returns Result.success(true)
        setupViewModel(SoraCardCommonVerification.Failed)
        viewModel.onAuthSucceed("accessToken")
        advanceUntilIdle()
        verify { mainRouter.openGetPrepared() }
    }

    @Test
    fun `on onKycFailed EXPECT navigate to verification failed screen`() = runTest {
        every { inMemoryRepo.isEnoughXorAvailable } returns true
        coEvery { kycRepository.hasFreeKycAttempt("accessToken") } returns Result.success(true)
        setupViewModel(SoraCardCommonVerification.Failed)
        val description = "description"
        viewModel.onKycFailed(statusDescription = description)
        advanceUntilIdle()
        verify { mainRouter.openVerificationFailed(description) }
    }
}