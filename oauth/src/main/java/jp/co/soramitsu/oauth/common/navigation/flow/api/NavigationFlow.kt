package jp.co.soramitsu.oauth.common.navigation.flow.api

import jp.co.soramitsu.oauth.common.navigation.flow.api.destinations.NavigationFlowDestination

interface NavigationFlow {

    fun start(fromDestination: NavigationFlowDestination)

    fun proceed()

    fun back()

    fun exit()
}
