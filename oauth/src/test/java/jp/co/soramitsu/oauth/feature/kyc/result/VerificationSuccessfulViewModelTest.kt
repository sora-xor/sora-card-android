package jp.co.soramitsu.oauth.feature.kyc.result

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.base.test.MainCoroutineRule
import jp.co.soramitsu.oauth.common.navigation.engine.activityresult.api.SetActivityResult
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
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.given
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class VerificationSuccessfulViewModelTest {

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var setActivityResult: SetActivityResult

    @Mock
    private lateinit var userSessionRepository: UserSessionRepository

    private lateinit var viewModel: VerificationSuccessfulViewModel

    @Before
    fun setUp() {
        viewModel = VerificationSuccessfulViewModel(
            setActivityResult = setActivityResult,
            userSessionRepository = userSessionRepository,
        )
    }

    @Test
    fun `init EXPECT toolbar title`() {
        assertEquals(R.string.verification_successful_title, viewModel.toolbarState.value?.basic?.title)
        assertNotNull(viewModel.toolbarState.value?.basic?.navIcon)
    }

    @Test
    fun `call onClose EXPECT finish kyc`() = runTest {
        given(userSessionRepository.getAccessToken()).willReturn("accessToken")
        given(userSessionRepository.getAccessTokenExpirationTime()).willReturn(Long.MAX_VALUE)
        given(userSessionRepository.getRefreshToken()).willReturn("refreshToken")

        viewModel.onClose()
        advanceUntilIdle()

        verify(setActivityResult).setResult(
            SoraCardResult.Success(
                accessToken = "accessToken",
                accessTokenExpirationTime = Long.MAX_VALUE,
                refreshToken = "refreshToken",
                SoraCardCommonVerification.Successful,
            )
        )
    }
}