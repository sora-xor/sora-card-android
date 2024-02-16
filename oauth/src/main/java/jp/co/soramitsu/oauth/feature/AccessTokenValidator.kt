package jp.co.soramitsu.oauth.feature

import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.common.model.AccessTokenResponse
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository

@Singleton
class AccessTokenValidator @Inject constructor(
    private val userSessionRepository: UserSessionRepository,
    private val pwoAuthClientProxy: PWOAuthClientProxy,
) {

    internal suspend fun checkAccessTokenValidity(): AccessTokenResponse? {
        val accessToken = userSessionRepository.getAccessToken()
        val accessTokenExpirationTime = userSessionRepository.getAccessTokenExpirationTime()
        val accessTokenExpired =
            accessTokenExpirationTime < TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())

        return if (accessToken.isBlank() || accessTokenExpired) {
            val response = getNewAccessToken()
            if (response is AccessTokenResponse.Token) {
                userSessionRepository.setNewAccessToken(response.token, response.expirationTime)
            }
            response
        } else {
            AccessTokenResponse.Token(accessToken, accessTokenExpirationTime)
        }
    }

    private suspend fun getNewAccessToken(): AccessTokenResponse? {
        val newTokenResult = pwoAuthClientProxy.getNewAccessToken("", "GET")
        val newTokenResultAccess = newTokenResult.accessTokenData
        if (newTokenResultAccess != null) {
            return AccessTokenResponse.Token(
                newTokenResultAccess.accessToken,
                newTokenResultAccess.accessTokenExpirationTime,
            )
        } else {
            val newTokenResultError = newTokenResult.errorData
            if (newTokenResultError != null) {
                return AccessTokenResponse.AuthError(newTokenResultError.error)
            } else {
                val newTokenResultSignIn = newTokenResult.userSignInRequired
                if (newTokenResultSignIn != null && newTokenResultSignIn) {
                    return AccessTokenResponse.SignInRequired
                }
            }
        }
        return null
    }
}
