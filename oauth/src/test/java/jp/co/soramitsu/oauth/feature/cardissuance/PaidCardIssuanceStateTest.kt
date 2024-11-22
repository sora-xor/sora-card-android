package jp.co.soramitsu.oauth.feature.cardissuance

import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.feature.cardissuance.state.PaidCardIssuanceState
import jp.co.soramitsu.oauth.uiscreens.compose.ScreenStatus
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PaidCardIssuanceStateTest {

    private lateinit var state: PaidCardIssuanceState

    @Before
    fun setUp() {
        PaidCardIssuanceState(
            screenStatus = ScreenStatus.LOADING,
            euroIssuanceAmount = "0",
        ).apply { state = this }
    }

    @Test
    fun `init EXPECT description is set up`() {
        assertEquals(
            R.string.card_issuance_screen_paid_card_description,
            (state.descriptionText as TextValue.StringRes).id,
        )
    }

    @Test
    fun `set state to ready to render EXPECT data is set correctly`() {
        val state = state.copy(screenStatus = ScreenStatus.READY_TO_RENDER)
        (state.titleText as TextValue.StringResWithArgs).let {
            assertEquals(R.string.card_issuance_screen_paid_card_title, it.id)
            assertArrayEquals(arrayOf(0.toString()), it.payload)
        }
        assertEquals(true, state.isPayIssuanceAmountButtonEnabled)
        (state.payIssuanceAmountText as TextValue.StringResWithArgs).let {
            assertEquals(R.string.card_issuance_screen_paid_card_pay_euro, it.id)
            assertArrayEquals(arrayOf(0.toString()), it.payload)
        }
    }

    @Test
    fun `set state to loading EXPECT data is not pasted`() {
        val state = state.copy(screenStatus = ScreenStatus.LOADING)
        assertEquals(R.string.cant_fetch_data, (state.titleText as TextValue.StringRes).id)
        assertEquals(false, state.isPayIssuanceAmountButtonEnabled)
        assertEquals(
            R.string.cant_fetch_data,
            (state.payIssuanceAmountText as TextValue.StringRes).id,
        )
    }
}
