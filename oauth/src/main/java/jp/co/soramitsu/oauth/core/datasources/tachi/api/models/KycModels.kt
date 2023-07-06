package jp.co.soramitsu.oauth.core.datasources.tachi.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class KycStatus {
    NotInitialized, Started, Completed, Failed, Rejected, Successful
}

enum class VerificationStatus {
    None, Pending, Accepted, Rejected
}

enum class IbanStatus {
    None, Pending, Rejected
}

@Serializable
data class KycResponse(
    @SerialName("kyc_id") val kycID: String,
    @SerialName("person_id") val personID: String,
    @SerialName("user_reference_number") val userReferenceNumber: String,
    @SerialName("reference_id") val referenceID: String,
    @SerialName("kyc_status") val kycStatus: KycStatus,
    @SerialName("verification_status") val verificationStatus: VerificationStatus,
    @SerialName("iban_status") val ibanStatus: IbanStatus,
    @SerialName("update_time") val updateTime: Int
)
