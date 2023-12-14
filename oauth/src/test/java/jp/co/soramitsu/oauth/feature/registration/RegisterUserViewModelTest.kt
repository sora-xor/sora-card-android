package jp.co.soramitsu.oauth.feature.registration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.domain.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterUserViewModelTest {

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var mainRouter: MainRouter

    private lateinit var viewModel: RegisterUserViewModel

    @Before
    fun setUp() {
        every { mainRouter.back() } just runs
        viewModel = RegisterUserViewModel(mainRouter)
    }

    @Test
    fun `init EXPECT toolbar title`() {
        assertEquals(R.string.user_registration_title, viewModel.toolbarState.value?.basic?.title)
    }

    @Test
    fun `init EXPECT set up first name state`() {
        assertEquals(
            R.string.user_registration_first_name_input_filed_label,
            viewModel.state.firstNameState.label,
        )
    }

    @Test
    fun `init EXPECT set up last name state`() {
        assertEquals(
            R.string.user_registration_last_name_input_filed_label,
            viewModel.state.lastNameState.label,
        )
    }

    @Test
    fun `init EXPECT set up button state`() {
        assertEquals(R.string.common_continue, viewModel.state.buttonState.title)
        assertFalse(viewModel.state.buttonState.enabled)
    }

    @Test
    fun `first name changed EXPECT update state`() {
        viewModel.onFirstNameChanged(TextFieldValue("firstName"))

        assertEquals(TextFieldValue("firstName"), viewModel.state.firstNameState.value)
    }

    @Test
    fun `last name changed EXPECT update state`() {
        viewModel.onLastNameChanged(TextFieldValue("lastName"))

        assertEquals(TextFieldValue("lastName"), viewModel.state.lastNameState.value)
    }

    @Test
    fun `first name is empty EXPECT confirm button is disabled`() {
        viewModel.onFirstNameChanged(TextFieldValue(""))

        assertFalse(viewModel.state.buttonState.enabled)
    }

    @Test
    fun `last name is empty EXPECT confirm button is disabled`() {
        viewModel.onLastNameChanged(TextFieldValue(""))

        assertFalse(viewModel.state.buttonState.enabled)
    }

    @Test
    fun `back EXPECT navigate back`() {
        viewModel.onToolbarNavigation()
        verify { mainRouter.back() }
    }
}
