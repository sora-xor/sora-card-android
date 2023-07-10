package jp.co.soramitsu.oauth.core.engines.activityresult.api

import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import com.paywings.onboarding.kyc.android.sdk.data.model.KycContractData
import java.util.Locale

interface ActivityResult {

    fun setKycContract(launcher: ActivityResultLauncher<KycContractData>)

    fun launchKycContract(kycContractData: KycContractData): Boolean

    fun setActivity(activity: Activity)

    fun removeActivity()

    fun setLocale(locale: Locale)

    fun setResult(soraCardResult: SoraCardResult): Boolean

    fun startOutwardsApp(appPackage: String, link: String): Boolean

}