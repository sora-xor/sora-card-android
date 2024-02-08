package jp.co.soramitsu.oauth.feature.verify.phone

import android.os.CountDownTimer
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.text.input.TextFieldValue
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
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.SoraCardEnvironmentType
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
class VerifyPhoneNumberViewModelTest {

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
    private lateinit var setActivityResult: SetActivityResult

    @MockK
    private lateinit var userSessionRepository: UserSessionRepository

    @MockK
    private lateinit var pwoAuthClientProxy: PWOAuthClientProxy

    @MockK
    private lateinit var inMemoryRepo: InMemoryRepo

    @MockK
    private lateinit var cdTimer: CountDownTimer

    @MockK
    private lateinit var timer: Timer

    private lateinit var viewModel: VerifyPhoneNumberViewModel

    @Before
    fun setUp() {
        every { inMemoryRepo.environment } returns SoraCardEnvironmentType.PRODUCTION
        every { timer.setOnTickListener(any()) } just runs
        every { timer.setOnFinishListener(any()) } just runs
        every { timer.start() } returns cdTimer
        every { mainRouter.back() } just runs
        coEvery { pwoAuthClientProxy.signInWithPhoneNumberVerifyOtp(any(), any()) } just runs
        coEvery { pwoAuthClientProxy.signInWithPhoneNumberRequestOtp(any(), any(), any(), any()) } just runs
        viewModel = VerifyPhoneNumberViewModel(
            mainRouter,
            userSessionRepository,
            timer,
            inMemoryRepo,
            pwoAuthClientProxy,
        )

        viewModel.setArgs(
            countryCode = "77",
            phoneNumber = "1111111",
            otpLength = 6,
            authCallback = authCallback,
        )
    }

    @Test
    fun `init EXPECT toolbar title`() {
        assertEquals(R.string.verify_phone_number_title, viewModel.toolbarState.value?.basic?.title)
    }

    @Test
    fun `init EXPECT set up input filed state`() {
        assertEquals(
            R.string.verify_phone_number_code_input_field_label,
            viewModel.state.value.inputTextState.label,
        )
    }

    @Test
    fun `init EXPECT set up button state`() {
        assertEquals(R.string.common_resend_code, viewModel.state.value.buttonState.title)
        assertFalse(viewModel.state.value.buttonState.enabled)
    }

    @Test
    fun `init viewModel EXPECT resend code button is disabled`() {
        assertFalse(viewModel.state.value.buttonState.enabled)
    }

    @Test
    fun `code length is bigger than max length EXPECT no state change`() {
        viewModel.onCodeChanged(TextFieldValue("123456"))
        viewModel.onCodeChanged(TextFieldValue("1234567"))

        assertEquals("123456", viewModel.state.value.inputTextState.value.text)
    }

    @Test
    fun `code length equals to otpLength EXPECT verify code`() = runTest {
        viewModel.onCodeChanged(TextFieldValue("123456"))
        advanceUntilIdle()

        assertTrue(viewModel.state.value.buttonState.loading)
    }

    @Test
    fun `code changed EXPECT input error state is false`() {
        viewModel.onCodeChanged(TextFieldValue("1236"))

        assertFalse(viewModel.state.value.inputTextState.error)
    }

    @Test
    fun `code changed EXPECT description text is empty`() {
        viewModel.onCodeChanged(TextFieldValue("1236"))

        assertEquals("", viewModel.state.value.inputTextState.descriptionText)
    }

    @Test
    fun `resend code EXPECT loading is true`() = runTest {
        viewModel.resendOtp()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.buttonState.loading)
    }

    @Test
    fun `on back EXPECT navigate back`() {
        viewModel.onToolbarNavigation()
        verify { mainRouter.back() }
    }
}
