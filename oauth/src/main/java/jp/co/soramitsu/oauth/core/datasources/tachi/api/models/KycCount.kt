package jp.co.soramitsu.oauth.core.datasources.tachi.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KycCount (
    @SerialName("total") val total: Int,
    @SerialName("completed") val completed: Int,
    @SerialName("rejected") val rejected: Int,
    @SerialName("free_attempt") val freeAttemptsLeft: Boolean
)