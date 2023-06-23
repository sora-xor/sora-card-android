package jp.co.soramitsu.oauth.feature.verify.phone

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.text.input.TextFieldValue
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.test.MainCoroutineRule
import jp.co.soramitsu.oauth.feature.KycCallback
import jp.co.soramitsu.oauth.feature.OAuthCallback
import jp.co.soramitsu.oauth.core.datasources.session.api.UserSessionRepository
import jp.co.soramitsu.oauth.feature.login.conditions.phone.VerifyPhoneNumberViewModel
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
class VerifyPhoneNumberViewModelTest {

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
    private lateinit var kycCallback: KycCallback

    @Mock
    private lateinit var userSessionRepository: UserSessionRepository

    @Mock
    private lateinit var inMemoryRepo: InMemoryRepo

    @Mock
    private lateinit var timer: Timer

    private lateinit var viewModel: VerifyPhoneNumberViewModel

    @Before
    fun setUp() {
        viewModel = VerifyPhoneNumberViewModel(
            mainRouter,
            userSessionRepository,
            timer,
            inMemoryRepo,
        )

        viewModel.setArgs(
            phoneNumber = "1111111",
            otpLength = 6,
            authCallback = authCallback,
            kycCallback = kycCallback
        )
    }

    @Test
    fun `init EXPECT toolbar title`() {
        assertEquals(R.string.verify_phone_number_title, viewModel.toolbarState.value?.basic?.title)
    }

    @Test
    fun `init EXPECT set up input filed state`() {
        assertEquals(R.string.verify_phone_number_code_input_field_label, viewModel.state.inputTextState.label)
    }

    @Test
    fun `init EXPECT set up button state`() {
        assertEquals(R.string.common_resend_code, viewModel.state.buttonState.title)
        assertFalse(viewModel.state.buttonState.enabled)
    }

    @Test
    fun `init viewModel EXPECT resend code button is disabled`() {
        assertFalse(viewModel.state.buttonState.enabled)
    }

    @Test
    fun `code length is bigger than max length EXPECT no state change`() {
        viewModel.onCodeChanged(TextFieldValue("123456"))
        viewModel.onCodeChanged(TextFieldValue("1234567"))

        assertEquals("123456", viewModel.state.inputTextState.value.text)
    }

    @Test
    fun `code length equals to otpLength EXPECT verify code`() = runTest {
        viewModel.onCodeChanged(TextFieldValue("123456"))
        advanceUntilIdle()

        assertTrue(viewModel.state.buttonState.loading)
    }

    @Test
    fun `code changed EXPECT input error state is false`() {
        viewModel.onCodeChanged(TextFieldValue("1236"))

        assertFalse(viewModel.state.inputTextState.error)
    }

    @Test
    fun `code changed EXPECT description text is empty`() {
        viewModel.onCodeChanged(TextFieldValue("1236"))

        assertEquals("", viewModel.state.inputTextState.descriptionText)
    }

    @Test
    fun `resend code EXPECT loading is true`() = runTest {
        viewModel.resendOtp()
        advanceUntilIdle()

        assertTrue(viewModel.state.buttonState.loading)
    }

    @Test
    fun `on back EXPECT navigate back`() {
        viewModel.onToolbarNavigation()

        verify(mainRouter).back()
    }
}
