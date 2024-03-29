package jp.co.soramitsu.oauth.feature.verify.phone

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.text.input.TextFieldValue
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.test.MainCoroutineRule
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
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
class EnterPhoneNumberViewModelTest {

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var mainRouter: MainRouter

    @Mock
    private lateinit var pwoAuthClientProxy: PWOAuthClientProxy

    @Mock
    private lateinit var inMemoryRepo: InMemoryRepo

    private lateinit var viewModel: EnterPhoneNumberViewModel

    @Before
    fun setUp() {
        viewModel = EnterPhoneNumberViewModel(
            mainRouter,
            inMemoryRepo,
            pwoAuthClientProxy,
        )
    }

    @Test
    fun `init EXPECT toolbar title`() {
        assertEquals(R.string.verify_phone_number_title, viewModel.toolbarState.value?.basic?.title)
    }

    @Test
    fun `init EXPECT set up input field state`() {
        assertEquals(R.string.enter_phone_number_phone_input_field_label, viewModel.state.inputTextState.label)
        assertEquals(R.string.common_no_spam, viewModel.state.inputTextState.descriptionText)
    }

    @Test
    fun `init EXPECT set up button state`() {
        assertEquals(R.string.common_send_code, viewModel.state.buttonState.title)
        assertFalse(viewModel.state.buttonState.enabled)
    }

    @Test
    fun `phone length is bigger than max length EXPECT no state change`() {
        viewModel.onPhoneChanged(TextFieldValue("333333"))
        viewModel.onPhoneChanged(TextFieldValue("3333333333333333"))

        assertEquals("333333", viewModel.state.inputTextState.value.text)
    }

    @Test
    fun `phone comprises non digit symbols EXPECT filter phone`() {
        viewModel.onPhoneChanged(TextFieldValue("3333%33@"))

        assertEquals("333333", viewModel.state.inputTextState.value.text)
    }

    @Test
    fun `phone changed EXPECT input state value updated`() {
        viewModel.onPhoneChanged(TextFieldValue("333333"))

        assertEquals("333333", viewModel.state.inputTextState.value.text)
    }

    @Test
    fun `phone changed EXPECT error state is false`() {
        viewModel.onPhoneChanged(TextFieldValue("333333"))

        assertFalse(viewModel.state.inputTextState.error)
    }

    @Test
    fun `phone changed EXPECT description text is default`() {
        viewModel.onPhoneChanged(TextFieldValue("333333"))

        assertEquals(
            R.string.common_no_spam,
            viewModel.state.inputTextState.descriptionText
        )
    }

    @Test
    fun `phone changed EXPECT confirm button is enabled`() {
        viewModel.onPhoneChanged(TextFieldValue("333333"))

        assertTrue(viewModel.state.buttonState.enabled)
    }

    @Test
    fun `request code EXPECT loading is true`() = runTest {
        viewModel.onRequestCode()
        advanceUntilIdle()

        assertTrue(viewModel.state.buttonState.loading)
    }

    @Test
    fun `on back EXPECT navigate back`() {
        viewModel.onToolbarNavigation()

        verify(mainRouter).back()
    }
}
