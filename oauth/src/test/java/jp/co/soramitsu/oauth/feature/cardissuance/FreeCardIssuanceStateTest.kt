package jp.co.soramitsu.oauth.feature.cardissuance

import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.ScreenStatus
import jp.co.soramitsu.oauth.base.compose.Text
import jp.co.soramitsu.oauth.feature.verification.result.cardissuance.state.FreeCardIssuanceState
import org.junit.Assert
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
            euroLiquidityThreshold = 100.toDouble()
        ).apply { state = this }
    }

    @Test
    fun `init EXPECT title and description set up`() {
        Assert.assertEquals(
            Text.StringRes(R.string.card_issuance_screen_free_card_title),
            state.titleText
        )

        Assert.assertEquals(
            Text.StringResWithArgs(
                id = R.string.card_issuance_screen_free_card_description,
                payload = arrayOf(100.toString())
            ),
            state.descriptionText
        )
    }

    @Test
    fun `set state to ready to render EXPECT data is set correct`() {
        val state = state.copy(screenStatus = ScreenStatus.READY_TO_RENDER)

        Assert.assertEquals(
            Text.StringResWithArgs(
                id = R.string.details_need_xor_desription,
                payload = arrayOf(
                    String.format("%.2f", .0f),
                    String.format("%.2f", .0f)
                )
            ),
            state.xorSufficiencyText
        )

        Assert.assertEquals(
            1f,
            state.xorSufficiencyPercentage
        )

        Assert.assertEquals(
            true,
            state.isGetInsufficientXorButtonEnabled
        )

        Assert.assertEquals(
            Text.StringResWithArgs(
                id = R.string.card_issuance_screen_free_card_get_xor,
                payload = arrayOf(
                    String.format("%.2f", .0f)
                )
            ),
            state.getInsufficientXorText
        )
    }

    @Test
    fun `set state to loading EXPECT data is not pasted`() {
        val state = state.copy(screenStatus = ScreenStatus.LOADING)

        Assert.assertEquals(
            Text.StringRes(id = R.string.cant_fetch_data),
            state.xorSufficiencyText
        )

        Assert.assertEquals(
            0f,
            state.xorSufficiencyPercentage
        )

        Assert.assertEquals(
            false,
            state.isGetInsufficientXorButtonEnabled
        )

        Assert.assertEquals(
            Text.StringRes(
                id = R.string.card_issuance_screen_free_card_get_xor
            ),
            state.getInsufficientXorText
        )
    }
}