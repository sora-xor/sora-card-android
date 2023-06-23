package jp.co.soramitsu.oauth.feature.verification.result.prepared.model

import androidx.compose.runtime.Stable

@Stable
data class GetPreparedState(
    val attemptCost: Int = 12,
    val steps: List<Step> = emptyList(),
)
