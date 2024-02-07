package jp.co.soramitsu.oauth.feature.kyc.result.verificationrejection

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.navigation.SetActivityResult
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.domain.PriceInteractor
import jp.co.soramitsu.oauth.common.model.KycAttemptsDto
import jp.co.soramitsu.oauth.common.model.XorEuroPrice
import jp.co.soramitsu.oauth.common.model.emptyKycResponse
import jp.co.soramitsu.oauth.domain.MainCoroutineRule
import jp.co.soramitsu.oauth.feature.kyc.result.verificationrejected.VerificationRejectedViewModel
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
class VerificationRejectedViewModelTest {

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
    private lateinit var setActivityResult: SetActivityResult

    @MockK
    private lateinit var userSessionRepository: UserSessionRepository

    @MockK
    private lateinit var kycRepository: KycRepository

    @MockK
    private lateinit var priceInteractor: PriceInteractor

    private lateinit var kycCountAttemptsUnavailable: KycAttemptsDto

    private lateinit var xorEuroPrice: XorEuroPrice

    @Before
    fun setUp() {
        every { mainRouter.openSupportChat() } just runs
        every { mainRouter.openGetPrepared() } just runs
        kycCountAttemptsUnavailable = KycAttemptsDto(
            total = 3,
            completed = 3,
            rejected = 1,
            freeAttemptAvailable = false,
            freeAttemptsCount = 3,
            totalFreeAttemptsCount = 4,
        )

        xorEuroPrice = XorEuroPrice(
            pair = "test pair",
            price = "1.0",
            source = "test source",
            timeOfUpdate = 7,
        )
    }

    @Test
    fun `init EXPECT toolbar title`() {
        val viewModel = VerificationRejectedViewModel(
            mainRouter = mainRouter,
            userSessionRepository = userSessionRepository,
            kycRepository = kycRepository,
            setActivityResult = setActivityResult,
            priceInteractor = priceInteractor,
        )

        assertEquals(
            R.string.verification_rejected_title,
            viewModel.toolbarState.value?.basic?.title,
        )
        assertNotNull(viewModel.toolbarState.value?.basic?.navIcon)
    }

    @Test
    fun `try again on available attempts EXPECT main router navigate to get prepared screen`() =
        runTest {
            val kycCountAttemptsAvailable = KycAttemptsDto(
                total = 3,
                completed = 1,
                rejected = 1,
                freeAttemptAvailable = true,
                freeAttemptsCount = 2,
                totalFreeAttemptsCount = 4,
            )
            coEvery { userSessionRepository.getAccessToken() } returns "Token"
            every {
                kycRepository.getCachedKycResponse()
            } returns (SoraCardCommonVerification.Rejected to emptyKycResponse)
            coEvery { kycRepository.getFreeKycAttemptsInfo(any()) } returns Result.success(
                kycCountAttemptsAvailable,
            )
            coEvery { priceInteractor.calculateKycAttemptPrice() } returns "3.80"

            val viewModel = VerificationRejectedViewModel(
                mainRouter = mainRouter,
                userSessionRepository = userSessionRepository,
                kycRepository = kycRepository,
                setActivityResult = setActivityResult,
                priceInteractor = priceInteractor,
            )
            advanceUntilIdle()

            viewModel.onTryAgain()
            advanceUntilIdle()

            verify { mainRouter.openGetPrepared() }
            verify(exactly = 0) { setActivityResult.setResult(any()) }
        }

    @Test
    fun `open telegram support EXPECT navigate to support chat`() = runTest {
        val viewModel = VerificationRejectedViewModel(
            mainRouter = mainRouter,
            userSessionRepository = userSessionRepository,
            kycRepository = kycRepository,
            setActivityResult = setActivityResult,
            priceInteractor = priceInteractor,
        )

        viewModel.openTelegramSupport()
        advanceUntilIdle()
        verify { mainRouter.openSupportChat() }
    }
}
