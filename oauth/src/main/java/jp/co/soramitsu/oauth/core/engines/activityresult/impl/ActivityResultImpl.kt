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

    private var activityWeakRef: WeakReference<Activity>? = null
        get() = if (!Looper.getMainLooper().isCurrentThread)
            throw IllegalAccessError(NOT_MAIN_THREAD_ACCESS) else
                field

    override fun setActivity(activity: Activity) {
        if (!Looper.getMainLooper().isCurrentThread)
            throw IllegalAccessError(NOT_MAIN_THREAD_ACCESS)

        activityWeakRef = WeakReference(activity)
    }

    override fun setResult(soraCardResult: SoraCardResult): Boolean {
        activityWeakRef?.get()?.apply {
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
        activityWeakRef?.get()?.isAppAvailableCompat(appPackage) ?: return false

        activityWeakRef?.get()?.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(link)
            )
        ) ?: return false

        return true
    }

    private companion object {
        const val NOT_MAIN_THREAD_ACCESS =
            "Access to WeakRef<Activity> is allowed only from one thread for safe memory leaklessness"
    }

}