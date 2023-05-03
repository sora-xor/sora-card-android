package jp.co.soramitsu.oauth.feature.cardissuance

import jp.co.soramitsu.oauth.base.compose.Text
import jp.co.soramitsu.oauth.feature.cardissuance.state.PaidCardIssuanceState
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import jp.co.soramitsu.oauth.R

@RunWith(MockitoJUnitRunner::class)
class PaidCardIssuanceStateTest {

    private lateinit var state: PaidCardIssuanceState

    @Before
    fun setUp() {
        PaidCardIssuanceState(
            issuanceAmount = 0
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
    fun `wrong issuance amount set EXPECT can't fetch data title text set`() {
        state = state.copy(issuanceAmount = 0)

        Assert.assertEquals(
            Text.StringRes(
                id = R.string.cant_fetch_data
            ),
            state.titleText
        )

        state = state.copy(issuanceAmount = -1)

        Assert.assertEquals(
            Text.StringRes(
                id = R.string.cant_fetch_data
            ),
            state.titleText
        )
    }

    @Test
    fun `correct issuance amount set EXPECT title text set`() {
        state = state.copy(issuanceAmount = 1)

        Assert.assertEquals(
            Text.StringResWithArgs(
                id = R.string.card_issuance_screen_paid_card_title,
                payload = arrayOf(1.toString())
            ),
            state.titleText
        )
    }

    @Test
    fun `wrong issuance amount set EXPECT can't fetch data pay issuance amount text set`() {
        state = state.copy(issuanceAmount = 0)

        Assert.assertEquals(
            Text.StringRes(
                id = R.string.cant_fetch_data
            ),
            state.payIssuanceAmountText
        )

        Assert.assertEquals(false, state.shouldPayIssuanceAmountButtonBeEnabled)

        state = state.copy(issuanceAmount = -1)

        Assert.assertEquals(
            Text.StringRes(
                id = R.string.cant_fetch_data
            ),
            state.payIssuanceAmountText
        )

        Assert.assertEquals(false, state.shouldPayIssuanceAmountButtonBeEnabled)
    }

    @Test
    fun `correct issuance amount set EXPECT pay issuance amount text set`() {
        state = state.copy(issuanceAmount = 1)

        Assert.assertEquals(
            Text.StringResWithArgs(
                id = R.string.card_issuance_screen_paid_card_pay_euro,
                payload = arrayOf(1.toString())
            ),
            state.payIssuanceAmountText
        )

        Assert.assertEquals(true, state.shouldPayIssuanceAmountButtonBeEnabled)
    }
}