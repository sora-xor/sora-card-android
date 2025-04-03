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
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.androidfoundation.format.unsafeCast
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.navigation.SetActivityResult
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.SoraCardEnvironmentType
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardFlow
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.common.model.CountryDial
import jp.co.soramitsu.oauth.domain.MainCoroutineRule
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.oauth.feature.telephone.LocaleService
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

    @MockK
    private lateinit var setActivityResult: SetActivityResult

    @MockK
    private lateinit var localeService: LocaleService

    @MockK
    private lateinit var kycRepository: KycRepository

    @MockK
    private lateinit var userSessionRepository: UserSessionRepository

    private lateinit var viewModel: EnterPhoneNumberViewModel

    @Before
    fun setUp() {
        every { inMemoryRepo.environment } returns SoraCardEnvironmentType.PRODUCTION
        coEvery { pwoAuthClientProxy.signInWithPhoneNumberRequestOtp(any(), any(), any(), any()) } just runs
        every { mainRouter.back() } just runs
        every { mainRouter.openCountryList(true) } just runs
        every { localeService.code } returns "US"
        every { setActivityResult.setResult(any()) } just runs
        coEvery { kycRepository.getCountries() } returns listOf(CountryDial("US", "USA", "+1"))
        every { inMemoryRepo.flow!!.unsafeCast<SoraCardFlow.SoraCardKycFlow>().logIn } returns false
        coEvery { userSessionRepository.setPhoneNumber(any()) } just runs
        viewModel = EnterPhoneNumberViewModel(
            mainRouter,
            pwoAuthClientProxy,
            localeService,
            kycRepository,
            setActivityResult,
            inMemoryRepo,
            userSessionRepository,
        )
    }

    @Test
    fun `signup with 0`() = runTest {
        every { inMemoryRepo.flow!!.unsafeCast<SoraCardFlow.SoraCardKycFlow>().logIn } returns false
        advanceUntilIdle()
        assertEquals("", viewModel.state.value.inputTextStateNumber.value.text)
        viewModel.onPhoneChanged(TextFieldValue("02834"))
        advanceUntilIdle()
        assertEquals("", viewModel.state.value.inputTextStateNumber.value.text)
    }

    @Test
    fun `login with 0`() = runTest {
        every { inMemoryRepo.flow!!.unsafeCast<SoraCardFlow.SoraCardKycFlow>().logIn } returns true
        advanceUntilIdle()
        viewModel.setLocale(null)
        advanceUntilIdle()
        assertEquals("", viewModel.state.value.inputTextStateNumber.value.text)
        viewModel.onPhoneChanged(TextFieldValue("02834"))
        advanceUntilIdle()
        assertEquals("02834", viewModel.state.value.inputTextStateNumber.value.text)
    }

    @Test
    fun `select country`() = runTest {
        advanceUntilIdle()
        viewModel.onSelectCountry()
        verify { mainRouter.openCountryList(true) }
    }

    @Test
    fun `init EXPECT toolbar title`() {
        assertEquals(R.string.verify_phone_number_title, viewModel.toolbarState.value?.basic?.title)
    }

    @Test
    fun `init EXPECT set up input field state`() {
        assertEquals(
            R.string.enter_phone_number_phone_input_field_label,
            viewModel.state.value.inputTextStateNumber.label,
        )
        assertEquals(
            R.string.common_no_spam,
            viewModel.state.value.inputTextStateNumber.descriptionText,
        )
    }

    @Test
    fun `init EXPECT set up button state`() {
        assertEquals(
            R.string.common_send_code,
            viewModel.state.value.buttonState.title.unsafeCast<TextValue.StringRes>().id,
        )
        assertFalse(viewModel.state.value.buttonState.enabled)
    }

    @Test
    fun `phone length is bigger than max length EXPECT no state change`() = runTest {
        advanceUntilIdle()
        viewModel.setLocale(null)
        advanceUntilIdle()
        viewModel.onPhoneChanged(TextFieldValue("333333"))
        viewModel.onPhoneChanged(TextFieldValue("3333333333333333333"))

        assertEquals("333333", viewModel.state.value.inputTextStateNumber.value.text)
        assertEquals("+1", viewModel.state.value.inputTextStateCode.value.text)
    }

    @Test
    fun `phone comprises non digit symbols EXPECT filter phone`() = runTest {
        advanceUntilIdle()
        viewModel.setLocale(null)
        advanceUntilIdle()
        viewModel.onPhoneChanged(TextFieldValue("3333%33@"))

        assertEquals("333333", viewModel.state.value.inputTextStateNumber.value.text)
    }

    @Test
    fun `phone changed EXPECT input state value updated`() = runTest {
        advanceUntilIdle()
        viewModel.setLocale(null)
        advanceUntilIdle()
        viewModel.onPhoneChanged(TextFieldValue("333333"))
        assertEquals("333333", viewModel.state.value.inputTextStateNumber.value.text)
    }

    @Test
    fun `phone changed EXPECT error state is false`() = runTest {
        advanceUntilIdle()
        viewModel.setLocale(null)
        advanceUntilIdle()
        viewModel.onPhoneChanged(TextFieldValue("333333"))

        assertFalse(viewModel.state.value.inputTextStateCode.error)
    }

    @Test
    fun `phone changed EXPECT description text is default`() = runTest {
        advanceUntilIdle()
        viewModel.setLocale(null)
        advanceUntilIdle()
        viewModel.onPhoneChanged(TextFieldValue("333333"))

        assertEquals(
            R.string.common_no_spam,
            viewModel.state.value.inputTextStateNumber.descriptionText,
        )
    }

    @Test
    fun `phone changed EXPECT confirm button is enabled`() = runTest {
        advanceUntilIdle()
        viewModel.setLocale(null)
        advanceUntilIdle()
        viewModel.onPhoneChanged(TextFieldValue("333333"))

        assertFalse(viewModel.state.value.buttonState.enabled)
    }

    @Test
    fun `request code EXPECT loading is true`() = runTest {
        advanceUntilIdle()
        viewModel.setLocale(null)
        advanceUntilIdle()
        viewModel.onRequestCode()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.buttonState.loading)
    }

    @Test
    fun `on back EXPECT navigate back`() {
        viewModel.onToolbarNavigation()
        verify { setActivityResult.setResult(SoraCardResult.Canceled) }
    }

    @Test
    fun `inputTextStateNumber is disabed while countryLoading is true`() =
        runTest {
            val initCountryLoadingState = true
            assertEquals(initCountryLoadingState, viewModel.state.value.countryLoading)
            assertEquals(false, viewModel.state.value.inputTextStateNumber.enabled)
            advanceUntilIdle()
        }

    @Test
    fun `inputTextStateNumber is enabled when countryLoading is false`() = runTest {
        advanceUntilIdle()
        viewModel.setLocale(null)
        advanceUntilIdle()
        assertEquals(false, viewModel.state.value.countryLoading)
        assertEquals(true, viewModel.state.value.inputTextStateNumber.enabled)
    }
}
