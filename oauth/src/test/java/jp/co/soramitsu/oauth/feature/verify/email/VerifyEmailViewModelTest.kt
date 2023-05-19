package jp.co.soramitsu.oauth.feature.verify.email

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.test.MainCoroutineRule
import jp.co.soramitsu.oauth.feature.OAuthCallback
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.oauth.feature.verify.Timer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
class VerifyEmailViewModelTest {

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var mainRouter: MainRouter

    @Mock
    private lateinit var authCallback: OAuthCallback

    @Mock
    private lateinit var userSessionRepository: UserSessionRepository

    @Mock
    private lateinit var timer: Timer

    private lateinit var viewModel: VerifyEmailViewModel

    @Before
    fun setUp() {
        viewModel = VerifyEmailViewModel(mainRouter, userSessionRepository, timer)
    }

    @Test
    fun `init EXPECT set up toolbar state`() {
        assertEquals(R.string.verify_email_title, viewModel.toolbarState.value?.basic?.title)
    }

    @Test
    fun `init EXPECT set up resend link button state`() {
        assertEquals(R.string.common_resend_link, viewModel.state.resendLinkButtonState.title)
        assertFalse(viewModel.state.resendLinkButtonState.enabled)
    }


    @Test
    fun `init EXPECT set up change email button state`() {
        assertEquals(R.string.common_change_email, viewModel.state.changeEmailButtonState.title)
        assertTrue(viewModel.state.changeEmailButtonState.enabled)
    }

    @Test
    fun `init EXPECT start resend link timer`() {
        verify(timer).start()
    }

    @Test
    fun `set args EXPECT update email`() {
        viewModel.setArgs(email = "email", autoEmailSend = true, authCallback = authCallback)

        assertEquals("email", viewModel.state.email)
    }

    @Test
    fun `resend link button clicked EXPECT loading state`() = runTest {
        viewModel.onResendLink()
        advanceUntilIdle()

        assertTrue(viewModel.state.resendLinkButtonState.loading)
        assertFalse(viewModel.state.resendLinkButtonState.enabled)
    }

    @Test
    fun `on change email EXPECT navigate`() {
        viewModel.onChangeEmail()

        verify(mainRouter).openChangeEmail()
    }

    @Test
    fun `back EXPECT navigate back`() {
        viewModel.onToolbarNavigation()

        verify(mainRouter).back()
    }
}
