package jp.co.soramitsu.oauth.feature.getmorexor

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import jp.co.soramitsu.oauth.common.navigation.engine.activityresult.api.SetActivityResult
import jp.co.soramitsu.oauth.common.navigation.flow.api.NavigationFlow
import jp.co.soramitsu.oauth.domain.MainCoroutineRule
import jp.co.soramitsu.oauth.feature.getmorexor.state.XorPurchaseMethod
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class GetMoreXorViewModelTest {

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var setActivityResult: SetActivityResult

    @MockK
    private lateinit var kycRequirementsUnfulfilledFlow: NavigationFlow

    private lateinit var viewModel: GetMoreXorViewModel

    @Before
    fun setUp() {
        every { kycRequirementsUnfulfilledFlow.back() } just runs
        every { setActivityResult.setResult(any()) } just runs
        viewModel = GetMoreXorViewModel(
            kycRequirementsUnfulfilledFlow = kycRequirementsUnfulfilledFlow,
            setActivityResult = setActivityResult,
        )
    }

    @Test
    fun `call onPurchaseMethodClicked EXPECT set activity result is called`() {
        XorPurchaseMethod.values().forEachIndexed { index, xorPurchaseMethod ->
            viewModel.onPurchaseMethodClicked(index)
            verify { setActivityResult.setResult(xorPurchaseMethod.mapToSoraCardNavigation()) }
        }
    }

    @Test
    fun `cancel dialog EXPECT back press behavior`() {
        viewModel.onCancelDialogClicked()
        verify { kycRequirementsUnfulfilledFlow.back() }
    }
}
