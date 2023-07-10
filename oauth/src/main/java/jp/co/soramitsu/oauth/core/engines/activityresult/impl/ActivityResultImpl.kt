package jp.co.soramitsu.oauth.core.engines.activityresult.impl

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Looper
import androidx.activity.result.ActivityResultLauncher
import com.paywings.onboarding.kyc.android.sdk.data.model.KycContractData
import jp.co.soramitsu.oauth.base.extension.isAppAvailableCompat
import jp.co.soramitsu.oauth.base.sdk.SoraCardConstants
import jp.co.soramitsu.oauth.core.engines.activityresult.api.SoraCardResult
import jp.co.soramitsu.oauth.core.engines.activityresult.api.ActivityResult
import java.lang.ref.WeakReference
import java.util.Locale
import javax.inject.Inject

class ActivityResultImpl @Inject constructor(): ActivityResult {

    private var kycContractRef: ActivityResultLauncher<KycContractData>? = null

    override fun setKycContract(launcher: ActivityResultLauncher<KycContractData>) {
        kycContractRef = launcher
    }

    override fun launchKycContract(kycContractData: KycContractData): Boolean {
        kycContractRef?.launch(kycContractData)
            ?: return false
        return true
    }

    private var activityRef: Activity? = null

    override fun setActivity(activity: Activity) {
        activityRef = activity
    }

    override fun removeActivity() {
        activityRef = null
    }

    override fun setLocale(locale: Locale) {
        Locale.setDefault(locale)

        activityRef?.run {
            val configuration = baseContext.resources.configuration
            configuration.setLocale(locale)
            configuration.setLayoutDirection(locale)

            baseContext.createConfigurationContext(configuration)
        }
    }

    override fun setResult(soraCardResult: SoraCardResult): Boolean {
        activityRef?.apply {
            setResult(
                Activity.RESULT_OK,
                Intent().apply {
                    putExtra(SoraCardConstants.EXTRA_SORA_CARD_RESULT, soraCardResult)
                }
            )
            finish()
        } ?: return false
        return true
    }

    override fun startOutwardsApp(appPackage: String, link: String): Boolean {
        activityRef?.isAppAvailableCompat(appPackage) ?: return false

        activityRef?.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(link)
            )
        ) ?: return false

        return true
    }

}