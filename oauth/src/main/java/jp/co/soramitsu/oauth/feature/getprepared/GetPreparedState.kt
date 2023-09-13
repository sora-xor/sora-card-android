package jp.co.soramitsu.oauth.feature.getprepared

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable

@Stable
data class GetPreparedState(
    val totalFreeAttemptsCount: String,
    val attemptCost: String,
    val steps: List<Step> = emptyList(),
)

data class Step(
    val index: Int,
    @StringRes val title: Int,
    val description: List<Int>,
)
