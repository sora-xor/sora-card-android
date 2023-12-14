package jp.co.soramitsu.oauth.common.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetReferenceNumberRequest(
    @SerialName("ReferenceID") val referenceID: String,
    @SerialName("MobileNumber") val mobileNumber: String?,
    @SerialName("Email") val email: String?,
    @SerialName("AddressChanged") val addressChanged: Boolean,
    @SerialName("DocumentChanged") val documentChanged: Boolean,
    @SerialName("AdditionalData") val additionalData: String,
)

@Serializable
data class GetReferenceNumberResponse(
    @SerialName("ReferenceID") val referenceID: String,
    @SerialName("CallerReferenceID") val callerReferenceID: String,
    @SerialName("ReferenceNumber") val referenceNumber: String,
    @SerialName("StatusCode") val statusCode: Int,
    @SerialName("StatusDescription") val statusDescription: String,
)
