package jp.co.soramitsu.oauth.core.engines.coroutines.impl

import jp.co.soramitsu.oauth.core.engines.coroutines.api.CoroutinesStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

class CoroutinesStorageImpl @Inject constructor(): CoroutinesStorage {

    override val unsupervisedUiScope: CoroutineScope =
        CoroutineScope(Job().plus(Dispatchers.Main))

    override val supervisedIoScope: CoroutineScope =
        CoroutineScope(SupervisorJob().plus(Dispatchers.IO))

    override val dispatcherMain: CoroutineDispatcher = Dispatchers.Main

    override val dispatcherIo: CoroutineDispatcher = Dispatchers.IO



}