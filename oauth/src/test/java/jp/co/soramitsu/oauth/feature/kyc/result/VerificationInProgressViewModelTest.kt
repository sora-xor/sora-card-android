package jp.co.soramitsu.oauth.feature.kyc.result

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.navigation.SetActivityResult
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.domain.MainCoroutineRule
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@OptIn(ExperimentalCoroutinesApi::class)
class VerificationInProgressViewModelTest {

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
    private lateinit var pwoAuthClientProxy: PWOAuthClientProxy

    @MockK
    private lateinit var mainRouter: MainRouter

    private lateinit var viewModel: VerificationInProgressViewModel

    @Before
    fun setUp() {
        every { mainRouter.openSupportChat() } just runs
        viewModel = VerificationInProgressViewModel(
            mainRouter = mainRouter,
            setActivityResult = setActivityResult,
            userSessionRepository = userSessionRepository,
            pwoAuthClientProxy = pwoAuthClientProxy,
        )
    }

    @Test
    fun `init EXPECT toolbar title`() {
        assertEquals(
            R.string.kyc_result_verification_in_progress,
            viewModel.toolbarState.value?.basic?.title,
        )
        assertNotNull(viewModel.toolbarState.value?.basic?.navIcon)
    }

    @Test
    fun `call openTelegramSupport EXPECT flow routes to telegram support`() = runTest {
        viewModel.openTelegramSupport()
        verify { mainRouter.openSupportChat() }
    }
}
