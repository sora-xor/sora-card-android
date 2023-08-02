package jp.co.soramitsu.oauth.base.sdk.contract

import android.os.Parcelable
import java.util.Locale
import jp.co.soramitsu.oauth.base.sdk.SoraCardEnvironmentType
import jp.co.soramitsu.oauth.base.sdk.SoraCardInfo
import jp.co.soramitsu.oauth.base.sdk.SoraCardKycCredentials
import kotlinx.parcelize.Parcelize

@Parcelize
data class SoraCardContractData(
    val locale: Locale,
    val apiKey: String,
    val domain: String,
    val kycCredentials: SoraCardKycCredentials,
    val environment: SoraCardEnvironmentType,
    val soraBackEndUrl: String,
    val client: String,
    val userAvailableXorAmount: Double,
    val areAttemptsPaidSuccessfully: Boolean,
    val isEnoughXorAvailable: Boolean,
    val isIssuancePaid: Boolean
) : Parcelable
