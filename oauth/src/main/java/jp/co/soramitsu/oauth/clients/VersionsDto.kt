package jp.co.soramitsu.oauth.clients

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class VersionsDto(
    @SerialName("android_sora_client_version") val sora: String,
    @SerialName("android_fearless_client_version") val fearless: String,
)
