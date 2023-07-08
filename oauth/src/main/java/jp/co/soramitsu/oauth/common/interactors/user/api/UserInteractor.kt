package jp.co.soramitsu.oauth.common.interactors.user.api

import kotlinx.coroutines.flow.StateFlow

interface UserInteractor {

    val resultFlow: StateFlow<UserOperationResult>

    suspend fun getUserData()

    suspend fun calculateFreeKycAttemptsLeft(): Result<Int>

}