package jp.co.soramitsu.oauth.feature

interface OAuthCallback {

    fun onOAuthSucceed(accessToken: String)

    fun onStartKyc()
}
