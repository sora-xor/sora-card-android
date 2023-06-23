package jp.co.soramitsu.oauth.feature.change.email

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.text.input.TextFieldValue
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.test.MainCoroutineRule
import jp.co.soramitsu.oauth.feature.registration.email.ChangeEmailViewModel
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
class ChangeEmailViewModelTest {

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var mainRouter: MainRouter

    private lateinit var viewModel: ChangeEmailViewModel

    @Before
    fun setUp() {
        viewModel = ChangeEmailViewModel(mainRouter)
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
        assertEquals(R.string.common_send_link, viewModel.state.buttonState.title)
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
    fun `send link EXPECT input filed is disabled`() = runTest {
        viewModel.onConfirm()
        advanceUntilIdle()

        assertFalse(viewModel.state.inputTextState.enabled)
    }

    @Test
    fun `send link EXPECT loading state`() = runTest {
        viewModel.onConfirm()
        advanceUntilIdle()

        assertTrue(viewModel.state.buttonState.loading)
    }

    @Test
    fun `back EXPECT navigate back`() {
        viewModel.onToolbarNavigation()

        verify(mainRouter).back()
    }
}
