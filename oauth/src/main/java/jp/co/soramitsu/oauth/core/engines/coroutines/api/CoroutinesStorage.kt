package jp.co.soramitsu.oauth.core.engines.coroutines.api

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

interface CoroutinesStorage {

    val unsupervisedUiScope: CoroutineScope

    val supervisedIoScope: CoroutineScope

    val dispatcherMain: CoroutineDispatcher

    val dispatcherIo: CoroutineDispatcher

}