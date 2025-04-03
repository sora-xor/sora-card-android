package jp.co.soramitsu.oauth.feature.verify.email

import android.os.CountDownTimer
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.service.callback.CheckEmailVerifiedCallback
import com.paywings.oauth.android.sdk.service.callback.SendNewVerificationEmailCallback
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.runs
import io.mockk.slot
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
        every { mainRouter.openEnterPhoneNumber(any()) } just runs
        coEvery { authCallback.onOAuthSucceed() } just runs
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
    fun `check email callback`() = runTest {
        val cb = slot<CheckEmailVerifiedCallback>()
        coEvery { pwoAuthClientProxy.checkEmailVerified(capture(cb)) } answers {
            cb.captured.onSignInSuccessful()
        }
        advanceUntilIdle()
        viewModel.setArgs("email", false, authCallback)
        advanceUntilIdle()
        coVerify { authCallback.onOAuthSucceed() }
    }

    @Test
    fun `check email callback onUserSignInRequired`() = runTest {
        val cb = slot<CheckEmailVerifiedCallback>()
        coEvery { pwoAuthClientProxy.checkEmailVerified(capture(cb)) } answers {
            cb.captured.onUserSignInRequired()
        }
        advanceUntilIdle()
        viewModel.setArgs("email", false, authCallback)
        advanceUntilIdle()
        coVerify { mainRouter.openEnterPhoneNumber(true) }
    }

    @Test
    fun `check email callback onError`() = runTest {
        val cb = slot<CheckEmailVerifiedCallback>()
        coEvery { pwoAuthClientProxy.checkEmailVerified(capture(cb)) } answers {
            cb.captured.onError(OAuthErrorCode.INVALID_EMAIL, "mess")
        }
        advanceUntilIdle()
        viewModel.setArgs("email", false, authCallback)
        advanceUntilIdle()
        val ds = viewModel.dialogState!!
        assertEquals("INVALID_EMAIL", (ds.title as TextValue.SimpleText).text)
        assertEquals(
            "Email is not a valid email address.",
            (ds.message as TextValue.SimpleText).text,
        )
    }

    @Test
    fun `send verification email onError`() = runTest {
        val cb = slot<SendNewVerificationEmailCallback>()
        coEvery { pwoAuthClientProxy.sendNewVerificationEmail(capture(cb)) } answers {
            cb.captured.onError(OAuthErrorCode.INVALID_EMAIL, "mess")
        }
        advanceUntilIdle()
        viewModel.onResendLink()
        advanceUntilIdle()
        val ds = viewModel.dialogState!!
        assertEquals("INVALID_EMAIL", (ds.title as TextValue.SimpleText).text)
        assertEquals(
            "Email is not a valid email address.",
            (ds.message as TextValue.SimpleText).text,
        )
    }

    @Test
    fun `send verification email onUserSignInRequired`() = runTest {
        val cb = slot<SendNewVerificationEmailCallback>()
        coEvery { pwoAuthClientProxy.sendNewVerificationEmail(capture(cb)) } answers {
            cb.captured.onUserSignInRequired()
        }
        advanceUntilIdle()
        viewModel.onResendLink()
        advanceUntilIdle()
        coVerify { mainRouter.openEnterPhoneNumber(true) }
    }

    @Test
    fun `send verification email onShowEmailConfirmationScreen`() = runTest {
        val cb = slot<SendNewVerificationEmailCallback>()
        coEvery { pwoAuthClientProxy.sendNewVerificationEmail(capture(cb)) } answers {
            cb.captured.onShowEmailConfirmationScreen("mail", true)
        }
        advanceUntilIdle()
        viewModel.onResendLink()
        advanceUntilIdle()
        verify { timer.start() }
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
