package jp.co.soramitsu.oauth.feature.cardissuance

import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.ScreenStatus
import jp.co.soramitsu.oauth.base.compose.Text
import jp.co.soramitsu.oauth.feature.cardissuance.state.FreeCardIssuanceState
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class FreeCardIssuanceStateTest {

    private lateinit var state: FreeCardIssuanceState

    @Before
    fun setUp() {
        FreeCardIssuanceState(
            screenStatus = ScreenStatus.LOADING,
            euroInsufficientAmount = 0.toDouble(),
            xorInsufficientAmount = 0.toDouble(),
            euroLiquidityThreshold = 100.toDouble(),
        ).apply { state = this }
    }

    @Test
    fun `init EXPECT title and description set up`() {
        assertEquals(
            R.string.card_issuance_screen_free_card_title,
            (state.titleText as Text.StringRes).id,
        )
        (state.descriptionText as Text.StringResWithArgs).let {
            assertEquals(R.string.card_issuance_screen_free_card_description, it.id)
            assertArrayEquals(arrayOf(100.toString()), it.payload)
        }
    }

    @Test
    fun `set state to ready to render EXPECT data is set correct`() {
        val state = state.copy(screenStatus = ScreenStatus.READY_TO_RENDER)
        (state.xorSufficiencyText as Text.StringResWithArgs).let {
            assertEquals(R.string.details_need_xor_desription, it.id)
            assertArrayEquals(
                arrayOf(
                    String.format("%.2f", .0f),
                    String.format("%.2f", .0f),
                ),
                it.payload,
            )
        }

        assertEquals(1f, state.xorSufficiencyPercentage)
        assertEquals(true, state.isGetInsufficientXorButtonEnabled)
        (state.getInsufficientXorText as Text.StringResWithArgs).let {
            assertEquals(R.string.card_issuance_screen_free_card_get_xor, it.id)
            assertArrayEquals(
                arrayOf(
                    String.format("%.2f", .0f),
                ),
                it.payload,
            )
        }
    }

    @Test
    fun `set state to loading EXPECT data is not pasted`() {
        val state = state.copy(screenStatus = ScreenStatus.LOADING)
        assertEquals(R.string.cant_fetch_data, (state.xorSufficiencyText as Text.StringRes).id)

        assertEquals(0f, state.xorSufficiencyPercentage)
        assertEquals(false, state.isGetInsufficientXorButtonEnabled)
        assertEquals(
            R.string.card_issuance_screen_free_card_get_xor,
            (state.getInsufficientXorText as Text.StringRes).id,
        )
    }
}
