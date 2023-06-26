package jp.co.soramitsu.oauth.feature.cardissuance

import jp.co.soramitsu.oauth.base.compose.Text
import jp.co.soramitsu.oauth.feature.cardissuance.state.PaidCardIssuanceState
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.ScreenStatus

@RunWith(MockitoJUnitRunner::class)
class PaidCardIssuanceStateTest {

    private lateinit var state: PaidCardIssuanceState

    @Before
    fun setUp() {
        PaidCardIssuanceState(
            screenStatus = ScreenStatus.LOADING,
            euroIssuanceAmount = 0
        ).apply { state = this }
    }

    @Test
    fun `init EXPECT description is set up`() {
        Assert.assertEquals(
            Text.StringRes(
                id = R.string.card_issuance_screen_paid_card_description
            ),
            state.descriptionText
        )
    }

    @Test
    fun `set state to ready to render EXPECT data is set correctly`() {
        val state = state.copy(screenStatus = ScreenStatus.READY_TO_RENDER)

        Assert.assertEquals(
            Text.StringResWithArgs(
                id = R.string.card_issuance_screen_paid_card_title,
                payload = arrayOf(0.toString())
            ),
            state.titleText
        )

        Assert.assertEquals(
            true,
            state.isPayIssuanceAmountButtonEnabled
        )

        Assert.assertEquals(
            Text.StringResWithArgs(
                id = R.string.card_issuance_screen_paid_card_pay_euro,
                payload = arrayOf(0.toString())
            ),
            state.payIssuanceAmountText
        )
    }

    @Test
    fun `set state to loading EXPECT data is not pasted`() {
        val state = state.copy(screenStatus = ScreenStatus.LOADING)

        Assert.assertEquals(
            Text.StringRes(id = R.string.cant_fetch_data),
            state.titleText
        )

        Assert.assertEquals(
            false,
            state.isPayIssuanceAmountButtonEnabled
        )

        Assert.assertEquals(
            Text.StringRes(id = R.string.cant_fetch_data),
            state.payIssuanceAmountText
        )
    }
}