package jp.co.soramitsu.oauth.base.sdk.contract

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import jp.co.soramitsu.androidfoundation.intent.getParcelableCompat
import jp.co.soramitsu.oauth.base.CardActivity
import jp.co.soramitsu.oauth.base.sdk.SoraCardConstants
import jp.co.soramitsu.oauth.base.sdk.SoraCardConstants.BUNDLE_EXTRA_SORA_CARD_CONTRACT_DATA
import jp.co.soramitsu.oauth.base.sdk.SoraCardConstants.EXTRA_SORA_CARD_CONTRACT_DATA

class SoraCardContract :
    ActivityResultContract<SoraCardContractData, SoraCardResult>() {

    // Reason why we should use Bundle wrapper: https://stackoverflow.com/a/28589962
    override fun createIntent(context: Context, input: SoraCardContractData): Intent {
        val bundle = Bundle().apply {
            putParcelable(EXTRA_SORA_CARD_CONTRACT_DATA, input)
        }
        return Intent(context, CardActivity::class.java).apply {
            putExtra(BUNDLE_EXTRA_SORA_CARD_CONTRACT_DATA, bundle)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): SoraCardResult {
        return intent?.getParcelableCompat(
            SoraCardConstants.EXTRA_SORA_CARD_RESULT,
            SoraCardResult::class.java,
        ) ?: throw IllegalStateException("Sora Card SDK: No result data")
    }
}
