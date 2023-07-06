package jp.co.soramitsu.oauth.core.datasources.tachi.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * GetReferenceNumber POST request structure
 *
 *  @param referenceID Your unique reference ID.
 *  @param mobileNumber Required if: When mobile number is not verified through PayWings OAuth system.
 *  @param email User email address.
 *  @param addressChanged If set to true, user will have to enter address even if address data exists and is accepted.
 *  @param documentChanged If set to true, user will have to provide a document identification even if document data exists and is accepted.
 *  @param additionalData Additional data and/or instructions for vendors.
 *
 * Learn more from [GetReferenceNumber](https://onboarding-kyc-test.paywings.io/whitelabel/GetReferenceNumber).
 */
@Serializable
data class GetReferenceNumberRequest(
    @SerialName("ReferenceID") val referenceID: String,
    @SerialName("MobileNumber") val mobileNumber: String?,
    @SerialName("Email") val email: String?,
    @SerialName("AddressChanged") val addressChanged: Boolean,
    @SerialName("DocumentChanged") val documentChanged: Boolean,
    @SerialName("AdditionalData") val additionalData: String,
)

/**
 * GetReferenceNumber response structure
 *
 * @param referenceID PayWings response reference ID.
 * @param callerReferenceID Your request reference ID.
 * @param referenceNumber Reference number.
 * @param statusCode Status code.
 * @param statusDescription Status description.
 *
 * Learn more from [GetReferenceNumber](https://onboarding-kyc-test.paywings.io/whitelabel/GetReferenceNumber).
 */
@Serializable
data class GetReferenceNumberResponse(
    @SerialName("ReferenceID") val referenceID: String,
    @SerialName("CallerReferenceID") val callerReferenceID: String,
    @SerialName("ReferenceNumber") val referenceNumber: String,
    @SerialName("StatusCode") val statusCode: Int,
    @SerialName("StatusDescription") val statusDescription: String
)
