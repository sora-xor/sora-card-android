package jp.co.soramitsu.oauth.base.sdk.signin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import jp.co.soramitsu.oauth.base.CardActivity
import jp.co.soramitsu.oauth.base.extension.getParcelableCompat
import jp.co.soramitsu.oauth.base.sdk.SoraCardConstants
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardContractData
import jp.co.soramitsu.oauth.common.navigation.activityresult.api.SoraCardResult

class SoraCardSignInContract :
    ActivityResultContract<SoraCardContractData, SoraCardResult>() {

    // Reason why we should use Bundle wrapper: https://stackoverflow.com/a/28589962
    override fun createIntent(context: Context, input: SoraCardContractData): Intent {
        val bundle = Bundle().apply {
            putParcelable(SoraCardConstants.SIGN_IN_DATA, input)
        }
        return Intent(context, CardActivity::class.java).apply {
            putExtra(SoraCardConstants.SIGN_IN_BUNDLE_EXTRA, bundle)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): SoraCardResult {
        return intent?.getParcelableCompat(
            SoraCardConstants.EXTRA_SORA_CARD_RESULT,
            SoraCardResult::class.java
        ) ?: throw IllegalStateException("Sora Card SDK: No result data")
    }
}
