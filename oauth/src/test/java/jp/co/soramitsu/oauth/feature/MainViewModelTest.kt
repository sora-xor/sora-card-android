package jp.co.soramitsu.oauth.feature

import android.app.Activity
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.paywings.oauth.android.sdk.service.callback.GetUserDataCallback
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import java.util.Locale
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.navigation.SetActivityResult
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.SoraCardEnvironmentType
import jp.co.soramitsu.oauth.base.sdk.SoraCardKycCredentials
import jp.co.soramitsu.oauth.base.sdk.contract.IbanInfo
import jp.co.soramitsu.oauth.base.sdk.contract.IbanStatus
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardBasicContractData
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardContractData
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardFlow
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.common.model.AccessTokenResponse
import jp.co.soramitsu.oauth.domain.MainCoroutineRule
import jp.co.soramitsu.oauth.feature.gatehub.GateHubRepository
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
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
    private lateinit var pwoAuthClientProxy: PWOAuthClientProxy

    @MockK
    private lateinit var setActivityResult: SetActivityResult

    @MockK
    private lateinit var gateHubRepository: GateHubRepository

    @MockK
    private lateinit var activity: Activity

    @MockK
    private lateinit var inMemoryRepo: InMemoryRepo

    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() = runTest {
        coEvery { userSessionRepository.getAccessToken() } returns "accessToken"
        coEvery {
            userSessionRepository.getAccessTokenExpirationTime()
        } returns System.currentTimeMillis() + 300000
        coEvery { userSessionRepository.setNewAccessToken(any(), any()) } returns Unit
        every { mainRouter.openGetPrepared() } returns Unit
        every { mainRouter.openVerificationFailed(any()) } returns Unit
        every { mainRouter.openVerificationSuccessful() } just runs
        every { mainRouter.openWebUrl(any()) } just runs
        every { mainRouter.openGatehubOnboardingStep1() } just runs
        every { mainRouter.openTermsAndConditions() } just runs
        every { setActivityResult.setResult(any()) } just runs
        coEvery {
            tokenValidator.checkAccessTokenValidity()
        } returns AccessTokenResponse.Token("accessToken", 123000)
        coEvery {
            pwoAuthClientProxy.init(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } returns (true to "")
        every { inMemoryRepo.locale = any() } just runs
        every { inMemoryRepo.environment = any() } just runs
        every { inMemoryRepo.soraBackEndUrl = any() } just runs
        every { inMemoryRepo.client = any() } just runs
        every { inMemoryRepo.flow = any() } just runs
    }

    private fun setupViewModel(status: SoraCardCommonVerification) {
        coEvery { kycRepository.getKycLastFinalStatus("accessToken") } returns Result.success(status)
        viewModel = MainViewModel(
            userSessionRepository,
            kycRepository,
            mainRouter,
            inMemoryRepo,
            pwoAuthClientProxy,
            tokenValidator,
            setActivityResult,
            gateHubRepository,
        )
    }

    @Test
    fun `vm launch gatehub`() = runTest {
        setupViewModel(SoraCardCommonVerification.Failed)
        coEvery { gateHubRepository.onboarded() } returns Result.success(false)
        coEvery { gateHubRepository.getIframe() } returns Result.success("iurl")
        advanceUntilIdle()
        val data = SoraCardContractData(
            basic = SoraCardBasicContractData(
                apiKey = "",
                domain = "",
                environment = SoraCardEnvironmentType.TEST,
                platform = "",
                recaptcha = "",
            ),
            locale = Locale.ITALY,
            soraBackEndUrl = "",
            client = "",
            flow = SoraCardFlow.SoraCardGateHubFlow,
        )
        viewModel.launch(data, activity)
        advanceUntilIdle()
        verify { inMemoryRepo.flow = SoraCardFlow.SoraCardGateHubFlow }
        coVerify {
            pwoAuthClientProxy.init(
                activity,
                SoraCardEnvironmentType.TEST,
                "",
                domain = "",
                platform = "",
                recaptcha = "",
            )
        }
        verify { mainRouter.openGatehubOnboardingStep1() }
    }

    @Test
    fun `vm launch auth success`() = runTest {
        setupViewModel(SoraCardCommonVerification.Failed)
        coEvery { pwoAuthClientProxy.isSignIn() } returns true
        coEvery { kycRepository.getIbanStatus(any(), any()) } returns Result.success(IbanInfo("", IbanStatus.ACTIVE, ""))
        advanceUntilIdle()
        val flow = SoraCardFlow.SoraCardKycFlow(
            SoraCardKycCredentials("", "", ""),
            0.0,
            true,
            true,
            true,
            true,
        )
        val data = SoraCardContractData(
            basic = SoraCardBasicContractData(
                apiKey = "",
                domain = "",
                environment = SoraCardEnvironmentType.TEST,
                platform = "",
                recaptcha = "",
            ),
            locale = Locale.ITALY,
            soraBackEndUrl = "",
            client = "",
            flow = flow,
        )
        viewModel.launch(data, activity)
        advanceUntilIdle()
        verify { inMemoryRepo.flow = flow }
        coVerify {
            pwoAuthClientProxy.init(
                activity,
                SoraCardEnvironmentType.TEST,
                "",
                domain = "",
                platform = "",
                recaptcha = "",
            )
        }
        verify {
            setActivityResult.setResult(
                SoraCardResult.Success(SoraCardCommonVerification.IbanIssued),
            )
        }
    }

    @Test
    fun `vm launch terms and cond`() = runTest {
        setupViewModel(SoraCardCommonVerification.Failed)
        coEvery { pwoAuthClientProxy.isSignIn() } returns false
        coEvery { kycRepository.getIbanStatus(any(), any()) } returns Result.success(IbanInfo("", IbanStatus.ACTIVE, ""))
        coEvery { userSessionRepository.isTermsRead() } returns false
        advanceUntilIdle()
        val flow = SoraCardFlow.SoraCardKycFlow(
            SoraCardKycCredentials("", "", ""),
            0.0,
            true,
            true,
            true,
            true,
        )
        val data = SoraCardContractData(
            basic = SoraCardBasicContractData(
                apiKey = "",
                domain = "",
                environment = SoraCardEnvironmentType.TEST,
                platform = "",
                recaptcha = "",
            ),
            locale = Locale.ITALY,
            soraBackEndUrl = "",
            client = "",
            flow = flow,
        )
        viewModel.launch(data, activity)
        advanceUntilIdle()
        verify { inMemoryRepo.flow = flow }
        coVerify {
            pwoAuthClientProxy.init(
                activity,
                SoraCardEnvironmentType.TEST,
                "",
                domain = "",
                platform = "",
                recaptcha = "",
            )
        }
        verify { mainRouter.openTermsAndConditions() }
    }

    @Test
    fun `authCallback getUserData`() = runTest {
        coEvery { kycRepository.hasFreeKycAttempt("accessToken") } returns Result.success(true)
        val slot = slot<GetUserDataCallback>()
        coEvery { pwoAuthClientProxy.getUserData(capture(slot)) } answers {
            slot.captured.onUserData("", "", "", "", false, "")
        }
        coEvery {
            kycRepository.getReferenceNumber(any(), any(), any())
        } returns Result.success("refnumber")
        setupViewModel(SoraCardCommonVerification.Failed)
        advanceUntilIdle()
        viewModel.startKycProcess(activity)
    }

    @Test
    fun `no free kyc tries EXPECT show kyc requirements unfulfilled flow started`() = runTest {
        coEvery { kycRepository.hasFreeKycAttempt("accessToken") } returns Result.success(true)
        coEvery { kycRepository.getIbanStatus(any()) } returns Result.success(null)
        setupViewModel(SoraCardCommonVerification.Started)
        viewModel.onAuthSucceed()
        advanceUntilIdle()
        verify { mainRouter.openGetPrepared() }
    }

    @Test
    fun `free kyc tries available EXPECT show get prepared screen`() = runTest {
        coEvery { kycRepository.hasFreeKycAttempt("accessToken") } returns Result.success(true)
        coEvery { kycRepository.getIbanStatus(any()) } returns Result.success(null)
        setupViewModel(SoraCardCommonVerification.Failed)
        viewModel.onAuthSucceed()
        advanceUntilIdle()
        verify { mainRouter.openGetPrepared() }
    }
}
