package jp.co.soramitsu.oauth.feature.verification.result.prepared.model

import androidx.annotation.StringRes

data class Step(
    val index: Int,
    @StringRes val title: Int,
    @StringRes val description: Int
)
