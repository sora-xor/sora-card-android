package jp.co.soramitsu.oauth.core.engines.activityresult.impl

import android.app.Activity
import android.content.Intent
import android.os.Looper
import androidx.activity.result.ActivityResultLauncher
import com.paywings.onboarding.kyc.android.sdk.data.model.KycContractData
import com.paywings.onboarding.kyc.android.sdk.data.model.KycCredentials
import com.paywings.onboarding.kyc.android.sdk.data.model.KycSettings
import com.paywings.onboarding.kyc.android.sdk.data.model.KycUserData
import com.paywings.onboarding.kyc.android.sdk.data.model.UserCredentials
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.SoraCardConstants
import jp.co.soramitsu.oauth.core.engines.activityresult.api.SoraCardResult
import jp.co.soramitsu.oauth.core.engines.activityresult.api.ActivityResult
import java.lang.ref.WeakReference
import java.util.UUID
import javax.inject.Inject

class ActivityResultImpl @Inject constructor(): ActivityResult {

    private var kycContractWeakRef: WeakReference<ActivityResultLauncher<KycContractData>>? = null
        get() = if (!Looper.getMainLooper().isCurrentThread)
            throw IllegalAccessError(NOT_MAIN_THREAD_ACCESS) else
            field

    override fun setKycContract(launcher: ActivityResultLauncher<KycContractData>) {
        if (!Looper.getMainLooper().isCurrentThread)
            throw IllegalAccessError(NOT_MAIN_THREAD_ACCESS)

        kycContractWeakRef = WeakReference(launcher)
    }

    override fun launchKycContract(kycContractData: KycContractData): Boolean {
        kycContractWeakRef?.get()?.launch(kycContractData)
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

    private companion object {
        const val NOT_MAIN_THREAD_ACCESS =
            "Access to WeakRef<Activity> is allowed only from one thread for safe memory leaklessness"
    }

}