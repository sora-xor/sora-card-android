package jp.co.soramitsu.oauth.common.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IbanAccountResponseWrapper(
    @SerialName("ReferenceID") val referenceID: String,
    @SerialName("CallerReferenceID") val callerReferenceID: String,
    @SerialName("IBANs") val ibans: List<IbanAccountResponse>?,
    @SerialName("StatusCode") val statusCode: Int,
    @SerialName("StatusDescription") val statusDescription: String,
)

@Serializable
data class IbanAccountResponse(
    @SerialName("ID") val id: String,
    @SerialName("Iban") val iban: String,
    @SerialName("BicSwift") val bicSwift: String,
    @SerialName("BicSwiftForSepa") val bicSwiftForSepa: String,
    @SerialName("BicSwiftForSwift") val bicSwiftForSwift: String,
    @SerialName("Description") val description: String,
    @SerialName("Currency") val currency: String,
    @SerialName("CreatedDate") val createdDate: String,
    @SerialName("Status") val status: String,
    @SerialName("StatusDescription") val statusDescription: String,
    @SerialName("MinTransactionAmount") val minTransactionAmount: Long,
    @SerialName("MaxTransactionAmount") val maxTransactionAmount: Long,
    @SerialName("Balance") val balance: Long,
    @SerialName("AvailableBalance") val availableBalance: Long,
) {

    companion object {
        const val IBAN_ACCOUNT_ACTIVE_STATUS = "A"
        const val IBAN_ACCOUNT_SUSPENDED_BY_USER_STATUS = "U"
        const val IBAN_ACCOUNT_SUSPENDED_BY_SYSTEM_STATUS = "S"
        const val IBAN_ACCOUNT_CLOSED_STATUS = "C"
    }
}
