package jp.co.soramitsu.oauth.feature

data class MainScreenState(
    val kycUserData: KycUserData? = null,
    val userCredentials: UserCredentials? = null,
    val referenceNumber: String? = null,
)

data class MainScreenUiState(
    val loading: Boolean = false,
    val error: String? = null,
)
