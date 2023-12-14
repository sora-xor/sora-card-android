package jp.co.soramitsu.oauth.feature.getmorexor

import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.Text
import jp.co.soramitsu.oauth.feature.getmorexor.state.ChooseXorPurchaseMethodState
import jp.co.soramitsu.oauth.feature.getmorexor.state.XorPurchaseMethod
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ChooseXorPurchaseMethodStateTest {

    private lateinit var state: ChooseXorPurchaseMethodState

    @Before
    fun setUp() {
        ChooseXorPurchaseMethodState(
            xorPurchaseMethods = XorPurchaseMethod.values().toList(),
        ).apply { state = this }
    }

    @Test
    fun `init EXPECT string res are correct`() {
        assertEquals(R.string.details_get_more_xor, (state.titleText as Text.StringRes).id)
        assertEquals(
            R.string.get_more_xor_dialog_description,
            (state.descriptionText as Text.StringRes).id,
        )
        assertEquals(
            R.string.get_more_xor_dialog_deposit_option,
            (state.methodsTextList[0] as Text.StringRes).id,
        )
        assertEquals(
            R.string.get_more_xor_dialog_swap_option,
            (state.methodsTextList[1] as Text.StringRes).id,
        )
        // assertEquals(R.string.get_more_xor_dialog_buy_option, (state.methodsTextList[2] as Text.StringRes).id)
        assertEquals(R.string.common_cancel, (state.cancelText as Text.StringRes).id)
    }
}
