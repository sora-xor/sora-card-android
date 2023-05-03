package jp.co.soramitsu.oauth.common.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class XorEuroPrice(
    @SerialName("pair") val pair: String,
    @SerialName("price") val price: Double,
    @SerialName("source") val source: String,
    @SerialName("update_time") val timeOfUpdate: Int
)
