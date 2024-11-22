package jp.co.soramitsu.oauth.feature.verify.email

import android.os.CountDownTimer
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.androidfoundation.format.unsafeCast
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.domain.MainCoroutineRule
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

@OptIn(ExperimentalCoroutinesApi::class)
class VerifyEmailViewModelTest {

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var mainRouter: MainRouter

    @MockK
    private lateinit var authCallback: OAuthCallback

    @MockK
    private lateinit var userSessionRepository: UserSessionRepository

    @MockK
    private lateinit var pwoAuthClientProxy: PWOAuthClientProxy

    @MockK
    private lateinit var cdTimer: CountDownTimer

    @MockK
    private lateinit var timer: Timer

    private lateinit var viewModel: VerifyEmailViewModel

    @Before
    fun setUp() {
        every { timer.setOnTickListener(any()) } just runs
        every { timer.setOnFinishListener(any()) } just runs
        every { timer.start() } returns cdTimer
        every { mainRouter.back() } just runs
        every { mainRouter.openChangeEmail() } just runs
        coEvery { pwoAuthClientProxy.sendNewVerificationEmail(any()) } just runs
        viewModel = VerifyEmailViewModel(
            mainRouter,
            userSessionRepository,
            timer,
            pwoAuthClientProxy,
        )
    }

    @Test
    fun `init EXPECT set up toolbar state`() {
        assertEquals(R.string.verify_email_title, viewModel.toolbarState.value?.basic?.title)
    }

    @Test
    fun `init EXPECT set up resend link button state`() {
        assertEquals(
            R.string.common_resend_link,
            viewModel.state.resendLinkButtonState.title.unsafeCast<TextValue.StringRes>().id,
        )
        assertFalse(viewModel.state.resendLinkButtonState.enabled)
    }

    @Test
    fun `init EXPECT set up change email button state`() {
        assertEquals(
            R.string.common_change_email,
            viewModel.state.changeEmailButtonState.title.unsafeCast<TextValue.StringRes>().id,
        )
        assertTrue(viewModel.state.changeEmailButtonState.enabled)
    }

    @Test
    fun `init EXPECT start resend link timer`() {
        verify { timer.start() }
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
        verify { mainRouter.openChangeEmail() }
    }

    @Test
    fun `back EXPECT navigate back`() {
        viewModel.onToolbarNavigation()
        verify { mainRouter.back() }
    }
}
