package jp.co.soramitsu.oauth.base.sdk.contract

import android.os.Parcelable
import java.util.Locale
import jp.co.soramitsu.oauth.base.sdk.SoraCardEnvironmentType
import jp.co.soramitsu.oauth.base.sdk.SoraCardKycCredentials
import kotlinx.parcelize.Parcelize

@Parcelize
data class SoraCardContractData(
    val basic: SoraCardBasicContractData,
    val locale: Locale,
    val soraBackEndUrl: String,
    val client: String,
    val flow: SoraCardFlow,
) : Parcelable

@Parcelize
sealed interface SoraCardFlow : Parcelable {
    @Parcelize
    data class SoraCardKycFlow(
        val kycCredentials: SoraCardKycCredentials,
        val userAvailableXorAmount: Double,
        val areAttemptsPaidSuccessfully: Boolean,
        val isEnoughXorAvailable: Boolean,
        val isIssuancePaid: Boolean,
        var logIn: Boolean,
    ) : Parcelable, SoraCardFlow

    @Parcelize
    data object SoraCardGateHubFlow : SoraCardFlow
}

@Parcelize
data class SoraCardBasicContractData(
    val apiKey: String,
    val domain: String,
    val environment: SoraCardEnvironmentType,
    val platform: String,
    val recaptcha: String,
) : Parcelable
