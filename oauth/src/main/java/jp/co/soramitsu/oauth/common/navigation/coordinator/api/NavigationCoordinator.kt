package jp.co.soramitsu.oauth.common.navigation.coordinator.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

interface NavigationCoordinator {

    fun start(coroutineScope: CoroutineScope): Job

}