package jp.co.soramitsu.oauth.common.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KycAttemptsDto(
    @SerialName("total") val total: Int,
    @SerialName("completed") val completed: Int,
    @SerialName("rejected") val rejected: Int,
    @SerialName("free_attempt") val freeAttemptAvailable: Boolean,
    @SerialName("free_attempts_left") val freeAttemptsCount: Int,
    @SerialName("total_free_attempts") val totalFreeAttemptsCount: Int,
)
