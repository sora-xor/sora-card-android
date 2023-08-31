package jp.co.soramitsu.oauth.feature.kyc.result.verificationrejection

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.test.MainCoroutineRule
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.domain.PriceInteractor
import jp.co.soramitsu.oauth.common.model.KycAttemptsDto
import jp.co.soramitsu.oauth.common.model.XorEuroPrice
import jp.co.soramitsu.oauth.common.navigation.engine.activityresult.api.SetActivityResult
import jp.co.soramitsu.oauth.feature.kyc.result.verificationrejected.VerificationRejectedViewModel
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class VerificationRejectedViewModelTest {

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule(TestCoroutineDispatcher())

    @Mock
    private lateinit var mainRouter: MainRouter

    @Mock
    private lateinit var setActivityResult: SetActivityResult

    @Mock
    private lateinit var userSessionRepository: UserSessionRepository

    @Mock
    private lateinit var kycRepository: KycRepository

    @Mock
    private lateinit var priceInteractor: PriceInteractor

    private lateinit var viewModel: VerificationRejectedViewModel

    private lateinit var kycCountAttemptsAvailable: KycAttemptsDto

    private lateinit var kycCountAttemptsUnavailable: KycAttemptsDto

    private lateinit var xorEuroPrice: XorEuroPrice

    @Before
    fun setUp() {
        KycAttemptsDto(
            total = 3,
            completed = 1,
            rejected = 1,
            freeAttemptAvailable = true,
            freeAttemptsCount = 2,
        ).apply { kycCountAttemptsAvailable = this }

        KycAttemptsDto(
            total = 3,
            completed = 3,
            rejected = 1,
            freeAttemptAvailable = false,
            freeAttemptsCount = 3,
        ).apply { kycCountAttemptsUnavailable = this }

        XorEuroPrice(
            pair = "test pair",
            price = 1.0,
            source = "test source",
            timeOfUpdate = 7
        ).apply { xorEuroPrice = this }
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
            viewModel.toolbarState.value?.basic?.title
        )
        assertNotNull(viewModel.toolbarState.value?.basic?.navIcon)
    }

    @Test
    fun `try again on available attempts EXPECT main router navigate to get prepared screen`() =
        runTest {
            given(userSessionRepository.getAccessToken())
                .willReturn("Token")

            given(kycRepository.getFreeKycAttemptsInfo(any()))
                .willReturn(Result.success(kycCountAttemptsAvailable))

            given(priceInteractor.calculateKycAttemptPrice())
                .willReturn(Result.success(3.80))

            val viewModel = VerificationRejectedViewModel(
                mainRouter = mainRouter,
                userSessionRepository = userSessionRepository,
                kycRepository = kycRepository,
                setActivityResult = setActivityResult,
                priceInteractor = priceInteractor,
            )

            viewModel.onTryAgain()
            advanceUntilIdle()

            verify(mainRouter).openGetPrepared()
            verify(setActivityResult, times(0)).setResult(any())
        }

//    @Test
//    fun `try again on unavailable attempts EXPECT set activity result navigate to buy xor`() =
//        runTest {
//            val viewModel = VerificationRejectedViewModel(
//                mainRouter = mainRouter,
//                userSessionRepository = userSessionRepository,
//                kycRepository = kycRepository,
//                setActivityResult = setActivityResult,
//                priceInteractor = priceInteractor
//            )
//
//            viewModel.onTryAgain()
//            advanceUntilIdle()
//
//            verify(setActivityResult).setResult(SoraCardResult.NavigateTo(OutwardsScreen.BUY))
//            verify(mainRouter, times(0)).openGetPrepared()
//        }

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

        verify(mainRouter).openSupportChat()
    }
}
