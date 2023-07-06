package jp.co.soramitsu.oauth.base.navigation

interface OAuthCallback {

    fun onOAuthSucceed(accessToken: String)

    fun onStartKyc()
}
