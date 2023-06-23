package jp.co.soramitsu.oauth.base.sdk

import android.os.Parcelable
import com.paywings.oauth.android.sdk.data.enums.EnvironmentType
import kotlinx.parcelize.Parcelize

@Parcelize
data class SoraCardKycCredentials(
    val endpointUrl: String,
    val username: String,
    val password: String
) : Parcelable

@Parcelize
enum class SoraCardEnvironmentType : Parcelable {
    NOT_DEFINED,
    TEST,
    PRODUCTION
}

@Parcelize
data class SoraCardInfo(
    val accessToken: String,
    val accessTokenExpirationTime: Long,
    val refreshToken: String
) : Parcelable

fun SoraCardEnvironmentType.toPayWingsType(): EnvironmentType =
    when (this) {
        SoraCardEnvironmentType.NOT_DEFINED -> EnvironmentType.NOT_DEFINED
        SoraCardEnvironmentType.TEST -> EnvironmentType.TEST
        SoraCardEnvironmentType.PRODUCTION -> EnvironmentType.PRODUCTION
    }
