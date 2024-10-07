package jp.co.soramitsu.oauth.common.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class XorEuroPrice(
    @SerialName("pair") val pair: String,
    @SerialName("price") val price: String,
    @SerialName("source") val source: String,
    @SerialName("update_time") val timeOfUpdate: Long,
)

@Serializable
internal data class FeesDto(
    @SerialName("application_fee") val applicationFee: String,
    @SerialName("retry_fee") val retryFee: String,
)

@Serializable
internal data class CountryCodeDto(
    @SerialName("name") val countryName: String,
    @SerialName("dial_code") val dialCode: String,
)
