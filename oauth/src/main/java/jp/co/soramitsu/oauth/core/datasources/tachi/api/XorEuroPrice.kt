package jp.co.soramitsu.oauth.core.datasources.tachi.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class XorEuroPrice(
    @SerialName("pair") val pair: String,
    @SerialName("price") val price: Double,
    @SerialName("source") val source: String,
    @SerialName("update_time") val timeOfUpdate: Int
)
