package jp.co.soramitsu.oauth.feature.getmorexor

import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.common.navigation.engine.activityresult.api.SetActivityResult
import jp.co.soramitsu.oauth.common.navigation.flow.api.NavigationFlow
import jp.co.soramitsu.oauth.feature.getmorexor.state.XorPurchaseMethod
import jp.co.soramitsu.oauth.feature.kyc.result.verificationrejected.VerificationRejectedViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class GetMoreXorViewModelTest {

    @Mock
    private lateinit var setActivityResult: SetActivityResult

    @Mock
    private lateinit var kycRequirementsUnfulfilledFlow: NavigationFlow

    private lateinit var viewModel: GetMoreXorViewModel

    @Before
    fun setUp() {
        GetMoreXorViewModel(
            kycRequirementsUnfulfilledFlow = kycRequirementsUnfulfilledFlow,
            setActivityResult = setActivityResult
        ).apply { viewModel = this }
    }

    @Test
    fun `call onPurchaseMethodClicked EXPECT set activity result is called`() {
        XorPurchaseMethod.values().forEachIndexed { index, xorPurchaseMethod ->
            viewModel.onPurchaseMethodClicked(index)
            verify(setActivityResult).setResult(xorPurchaseMethod.mapToSoraCardNavigation())
        }
    }

    @Test
    fun `cancel dialog EXPECT back press behavior`() {
        viewModel.onCancelDialogClicked()
        verify(kycRequirementsUnfulfilledFlow).back()
    }
}