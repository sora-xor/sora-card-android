package jp.co.soramitsu.oauth.feature

data class KycUserData(
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val mobileNumber: String?,
)

data class UserCredentials(
    val accessToken: String,
    val refreshToken: String,
)
