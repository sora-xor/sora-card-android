package jp.co.soramitsu.oauth.common.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GetReferenceNumberRequest(
    @SerialName("AdditionalData") val additionalData: String?,
    @SerialName("AddressChanged") val addressChanged: Boolean?,
    @SerialName("CardTypeID") val cardTypeId: String?,
    @SerialName("DocumentChanged") val documentChanged: Boolean?,
    @SerialName("Email") val email: String?,
    @SerialName("IbanTypeID") val ibanTypeId: String?,
)

@Serializable
internal data class GetReferenceNumberResponse(
    @SerialName("ReferenceID") val referenceID: String?,
    @SerialName("CallerReferenceID") val callerReferenceID: String?,
    @SerialName("ReferenceNumber") val referenceNumber: String?,
    @SerialName("StatusCode") val statusCode: Int,
    @SerialName("StatusDescription") val statusDescription: String?,
)
