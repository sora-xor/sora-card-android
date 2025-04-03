package jp.co.soramitsu.oauth.feature.verify.email

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.text.input.TextFieldValue
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
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
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
class EnterEmailViewModelTest {

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
    private lateinit var userSessionRepository: UserSessionRepository

    @MockK
    private lateinit var pwoAuthClientProxy: PWOAuthClientProxy

    private lateinit var viewModel: EnterEmailViewModel

    @Before
    fun setUp() {
        coEvery { pwoAuthClientProxy.registerUser(any(), any(), any(), any()) } just runs
        every { mainRouter.back() } just runs
        viewModel = EnterEmailViewModel(
            mainRouter,
            userSessionRepository,
            pwoAuthClientProxy,
        )
    }

    @Test
    fun `init EXPECT toolbar title`() {
        assertEquals(R.string.enter_email_title, viewModel.toolbarState.value?.basic?.title)
    }

    @Test
    fun `init EXPECT set up input field state`() {
        assertEquals(R.string.enter_email_input_field_label, viewModel.state.inputTextState.label)
        assertEquals(R.string.common_no_spam, viewModel.state.inputTextState.descriptionText)
    }

    @Test
    fun `init EXPECT set up button state`() {
        assertEquals(
            R.string.common_send_link,
            viewModel.state.buttonState.title.unsafeCast<TextValue.StringRes>().id,
        )
    }

    @Test
    fun `email changed EXPECT update state`() {
        viewModel.onEmailChanged(TextFieldValue("test"))

        assertEquals(TextFieldValue("test"), viewModel.state.inputTextState.value)
    }

    @Test
    fun `email changed EXPECT error is false`() {
        viewModel.onEmailChanged(TextFieldValue("test"))

        assertFalse(viewModel.state.inputTextState.error)
    }

    @Test
    fun `email changed EXPECT description text is default`() {
        viewModel.onEmailChanged(TextFieldValue("test"))

        assertEquals(R.string.common_no_spam, viewModel.state.inputTextState.descriptionText)
    }

    @Test
    fun `email is not empty EXPECT confirm button is enabled`() {
        viewModel.onEmailChanged(TextFieldValue("test"))

        assertTrue(viewModel.state.buttonState.enabled)
    }

    @Test
    fun `email is empty EXPECT confirm button is disabled`() {
        viewModel.onEmailChanged(TextFieldValue(""))

        assertFalse(viewModel.state.buttonState.enabled)
    }

    @Test
    fun `register user EXPECT input filed is disabled`() = runTest {
        viewModel.onRegisterUser()
        advanceUntilIdle()

        assertFalse(viewModel.state.inputTextState.enabled)
    }

    @Test
    fun `register user EXPECT loading state`() = runTest {
        viewModel.onRegisterUser()
        advanceUntilIdle()

        assertTrue(viewModel.state.buttonState.loading)
    }

    @Test
    fun `back EXPECT navigate back`() {
        viewModel.onToolbarNavigation()
        verify { mainRouter.back() }
    }
}
