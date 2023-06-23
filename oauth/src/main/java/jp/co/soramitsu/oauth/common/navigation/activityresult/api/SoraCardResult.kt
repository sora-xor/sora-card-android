package jp.co.soramitsu.oauth.common.navigation.activityresult.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class SoraCardCommonVerification {
    Failed, Rejected, Pending, Successful, NoFreeAttempt
}

@Parcelize
sealed class SoraCardResult : Parcelable {

    @Parcelize
    data class Success(
        val accessToken: String,
        val accessTokenExpirationTime: Long,
        val refreshToken: String,
        val status: SoraCardCommonVerification,
    ) : SoraCardResult()

    @Parcelize
    data class Failure(
        val status: SoraCardCommonVerification? = null,
        val error: SoraCardError? = null
    ) : SoraCardResult()

    @Parcelize
    object Canceled : SoraCardResult()

    @Parcelize
    data class NavigateTo(
        val screen: OutwardsScreen
    ) : SoraCardResult()

}

enum class OutwardsScreen {
    DEPOSIT, SWAP, BUY
}

enum class SoraCardError {

    USER_NOT_FOUND,
    EMAIL_VERIFICATION_NOT_FINISHED
}
