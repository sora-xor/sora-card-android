package jp.co.soramitsu.oauth.feature.terms.and.conditions

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.base.test.MainCoroutineRule
import jp.co.soramitsu.oauth.common.navigation.engine.activityresult.api.SetActivityResult
import jp.co.soramitsu.oauth.feature.terms.and.conditions.model.WebUrl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
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
class TermsAndConditionsViewModelTest {

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var mainRouter: MainRouter

    @Mock
    private lateinit var setActivityResult: SetActivityResult

    private lateinit var viewModel: TermsAndConditionsViewModel

    @Before
    fun setUp() {
        viewModel = TermsAndConditionsViewModel(mainRouter, setActivityResult)
    }

    @Test
    fun `init EXPECT toolbar title`() {
        assertEquals(
            R.string.terms_and_conditions_title,
            viewModel.toolbarState.value?.basic?.title
        )
    }

    @Test
    fun `on general terms click EXPECT navigate`() {
        viewModel.onGeneralTermsClick()

        verify(mainRouter).openWebPage(
            titleRes = R.string.terms_and_conditions_general_terms,
            url = WebUrl.GENERAL_TERMS
        )
    }

    @Test
    fun `on privacy policy click EXPECT navigate`() {
        viewModel.onPrivacyPolicy()

        verify(mainRouter).openWebPage(
            titleRes = R.string.terms_and_conditions_privacy_policy,
            url = WebUrl.PRIVACY_POLICY
        )
    }

    @Test
    fun `on confirm click EXPECT navigate enter phone number`() {
        viewModel.onConfirm()

        verify(mainRouter).openEnterPhoneNumber()
    }

    @Test
    fun `back EXPECT finish kyc`() {
        viewModel.onToolbarNavigation()

        verify(setActivityResult).setResult(SoraCardResult.Canceled)
    }
}
