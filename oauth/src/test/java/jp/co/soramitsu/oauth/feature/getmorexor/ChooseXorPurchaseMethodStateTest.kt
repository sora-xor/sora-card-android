package jp.co.soramitsu.oauth.feature.getmorexor

import io.mockk.InternalPlatformDsl.toArray
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.Text
import jp.co.soramitsu.oauth.feature.verification.result.getmorexor.state.ChooseXorPurchaseMethodState
import jp.co.soramitsu.oauth.feature.verification.result.getmorexor.state.XorPurchaseMethod
import org.junit.Assert
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
            xorPurchaseMethods = XorPurchaseMethod.values().toList()
        ).apply { state = this }
    }

    @Test
    fun `init EXPECT string res are correct`() {
        Assert.assertEquals(
            state.titleText,
            Text.StringRes(R.string.details_get_more_xor)
        )

        Assert.assertEquals(
            state.descriptionText,
            Text.StringRes(R.string.get_more_xor_dialog_description)
        )

        Assert.assertArrayEquals(
            state.methodsTextList.toTypedArray(),
            arrayOf(
                Text.StringRes(R.string.get_more_xor_dialog_deposit_option),
                Text.StringRes(R.string.get_more_xor_dialog_swap_option),
                Text.StringRes(R.string.get_more_xor_dialog_buy_option)
            )
        )

        Assert.assertEquals(
            state.cancelText,
            Text.StringRes(R.string.common_cancel)
        )
    }

}