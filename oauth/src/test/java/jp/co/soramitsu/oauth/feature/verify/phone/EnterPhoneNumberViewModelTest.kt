package jp.co.soramitsu.oauth.feature.verify.phone

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
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.SoraCardEnvironmentType
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.domain.MainCoroutineRule
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
class EnterPhoneNumberViewModelTest {

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
    private lateinit var pwoAuthClientProxy: PWOAuthClientProxy

    @MockK
    private lateinit var inMemoryRepo: InMemoryRepo

    private lateinit var viewModel: EnterPhoneNumberViewModel

    @Before
    fun setUp() {
        every { inMemoryRepo.environment } returns SoraCardEnvironmentType.PRODUCTION
        coEvery { pwoAuthClientProxy.signInWithPhoneNumberRequestOtp(any(), any(), any()) } just runs
        every { mainRouter.back() } just runs
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
        assertEquals(R.string.enter_phone_number_phone_input_field_label, viewModel.state.value.inputTextState.label)
        assertEquals(R.string.common_no_spam, viewModel.state.value.inputTextState.descriptionText)
    }

    @Test
    fun `init EXPECT set up button state`() {
        assertEquals(R.string.common_send_code, viewModel.state.value.buttonState.title)
        assertFalse(viewModel.state.value.buttonState.enabled)
    }

    @Test
    fun `phone length is bigger than max length EXPECT no state change`() {
        viewModel.onPhoneChanged(TextFieldValue("333333"))
        viewModel.onPhoneChanged(TextFieldValue("3333333333333333333"))

        assertEquals("333333", viewModel.state.value.inputTextState.value.text)
    }

    @Test
    fun `phone comprises non digit symbols EXPECT filter phone`() {
        viewModel.onPhoneChanged(TextFieldValue("3333%33@"))

        assertEquals("333333", viewModel.state.value.inputTextState.value.text)
    }

    @Test
    fun `phone changed EXPECT input state value updated`() {
        viewModel.onPhoneChanged(TextFieldValue("333333"))

        assertEquals("333333", viewModel.state.value.inputTextState.value.text)
    }

    @Test
    fun `phone changed EXPECT error state is false`() {
        viewModel.onPhoneChanged(TextFieldValue("333333"))

        assertFalse(viewModel.state.value.inputTextState.error)
    }

    @Test
    fun `phone changed EXPECT description text is default`() {
        viewModel.onPhoneChanged(TextFieldValue("333333"))

        assertEquals(
            R.string.common_no_spam,
            viewModel.state.value.inputTextState.descriptionText
        )
    }

    @Test
    fun `phone changed EXPECT confirm button is enabled`() {
        viewModel.onPhoneChanged(TextFieldValue("333333"))

        assertFalse(viewModel.state.value.buttonState.enabled)
    }

    @Test
    fun `request code EXPECT loading is true`() = runTest {
        viewModel.onRequestCode()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.buttonState.loading)
    }

    @Test
    fun `on back EXPECT navigate back`() {
        viewModel.onToolbarNavigation()
        verify { mainRouter.back() }
    }
}
