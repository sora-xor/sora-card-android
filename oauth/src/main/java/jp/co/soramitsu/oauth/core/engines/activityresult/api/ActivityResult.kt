package jp.co.soramitsu.oauth.core.engines.activityresult.api

import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import com.paywings.onboarding.kyc.android.sdk.data.model.KycContractData
import com.paywings.onboarding.kyc.android.sdk.data.model.KycUserData
import com.paywings.onboarding.kyc.android.sdk.data.model.UserCredentials

interface ActivityResult {

    fun setKycContract(launcher: ActivityResultLauncher<KycContractData>)

    fun launchKycContract(kycContractData: KycContractData): Boolean

    fun setActivity(activity: Activity)

    fun setResult(soraCardResult: SoraCardResult): Boolean

}