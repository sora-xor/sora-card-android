package jp.co.soramitsu.oauth.feature.getmorexor

import jp.co.soramitsu.oauth.common.navigation.activityresult.api.ActivityResult
import jp.co.soramitsu.oauth.common.navigation.flow.api.NavigationFlow
import jp.co.soramitsu.oauth.feature.verification.result.getmorexor.state.XorPurchaseMethod
import jp.co.soramitsu.oauth.feature.verification.result.getmorexor.GetMoreXorViewModel
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class GetMoreXorViewModelTest {

    @Mock
    private lateinit var activityResult: ActivityResult

    @Mock
    private lateinit var kycRequirementsUnfulfilledFlow: NavigationFlow

    private lateinit var viewModel: GetMoreXorViewModel

    @Before
    fun setUp() {
        GetMoreXorViewModel(
            kycRequirementsUnfulfilledFlow = kycRequirementsUnfulfilledFlow,
            setActivityResult = activityResult
        ).apply { viewModel = this }
    }

    @Test
    fun `call onPurchaseMethodClicked EXPECT set activity result is called`() {
        XorPurchaseMethod.values().forEachIndexed { index, xorPurchaseMethod ->
            viewModel.onPurchaseMethodClicked(index)
            verify(activityResult).setResult(xorPurchaseMethod.mapToSoraCardNavigation())
        }
    }

    @Test
    fun `cancel dialog EXPECT back press behavior`() {
        viewModel.onCancelDialogClicked()
        verify(kycRequirementsUnfulfilledFlow).back()
    }
}