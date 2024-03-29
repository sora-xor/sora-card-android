package jp.co.soramitsu.oauth.common.navigation.engine.activityresult.impl

import android.content.Intent
import jp.co.soramitsu.oauth.base.mapSoraCardResult
import jp.co.soramitsu.oauth.base.sdk.SoraCardConstants
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.common.domain.CurrentActivityRetriever
import jp.co.soramitsu.oauth.common.navigation.engine.activityresult.api.SetActivityResult
import javax.inject.Inject

class SetActivityResultImpl @Inject constructor(
    private val currentActivityRetriever: CurrentActivityRetriever
) : SetActivityResult {

    override fun setResult(soraCardResult: SoraCardResult) {
        currentActivityRetriever.getCurrentActivity().apply {
            setResult(
                mapSoraCardResult(soraCardResult),
                Intent().apply {
                    putExtra(SoraCardConstants.EXTRA_SORA_CARD_RESULT, soraCardResult)
                }
            )
            finish()
        }
    }

}