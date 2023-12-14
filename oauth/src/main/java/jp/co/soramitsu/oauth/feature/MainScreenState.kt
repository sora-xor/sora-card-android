package jp.co.soramitsu.oauth.feature

import com.paywings.onboarding.kyc.android.sdk.data.model.KycUserData
import com.paywings.onboarding.kyc.android.sdk.data.model.UserCredentials

data class MainScreenState(
    val kycUserData: KycUserData? = null,
    val userCredentials: UserCredentials? = null,
    val referenceNumber: String? = null,
)

data class MainScreenUiState(
    val loading: Boolean = false,
)
