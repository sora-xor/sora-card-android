package jp.co.soramitsu.oauth.feature.kyc.result

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.base.test.MainCoroutineRule
import jp.co.soramitsu.oauth.common.navigation.engine.activityresult.api.SetActivityResult
import jp.co.soramitsu.oauth.feature.KycCallback
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class VerificationFailedViewModelTest {

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var setActivityResult: SetActivityResult

    @Mock
    private lateinit var userSessionRepository: UserSessionRepository

    @Mock
    private lateinit var kycCallback: KycCallback

    private lateinit var viewModel: VerificationFailedViewModel

    @Before
    fun setUp() {
        viewModel = VerificationFailedViewModel(
            setActivityResult = setActivityResult,
            userSessionRepository = userSessionRepository
        )
    }

    @Test
    fun `init EXPECT toolbar title`() {
        assertEquals(R.string.verification_failed_title, viewModel.toolbarState.value?.basic?.title)
        assertNotNull(viewModel.toolbarState.value?.basic?.navIcon)
    }

    @Test
    fun `call onClose EXPECT finish kyc`() {
        viewModel.setArgs(kycCallback)
        viewModel.onClose()

        verify(kycCallback).onFinish(
            result = SoraCardResult.Failure(SoraCardCommonVerification.Failed)
        )
    }
}