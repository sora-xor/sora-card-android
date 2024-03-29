package jp.co.soramitsu.oauth.feature.kyc.result

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.test.MainCoroutineRule
import jp.co.soramitsu.oauth.common.navigation.engine.activityresult.api.SetActivityResult
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class VerificationInProgressViewModelTest {

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var userSessionRepository: UserSessionRepository

    @Mock
    private lateinit var setActivityResult: SetActivityResult

    @Mock
    private lateinit var mainRouter: MainRouter

    private lateinit var viewModel: VerificationInProgressViewModel

    @Before
    fun setUp() {
        viewModel = VerificationInProgressViewModel(
            mainRouter = mainRouter,
            setActivityResult = setActivityResult,
            userSessionRepository = userSessionRepository,
        )
    }

    @Test
    fun `init EXPECT toolbar title`() {
        assertEquals(
            R.string.kyc_result_verification_in_progress,
            viewModel.toolbarState.value?.basic?.title
        )
        assertNotNull(viewModel.toolbarState.value?.basic?.navIcon)
    }

    @Test
    fun `call openTelegramSupport EXPECT flow routes to telegram support`() = runTest {
        viewModel.openTelegramSupport()

        verify(mainRouter).openSupportChat()
    }
}
