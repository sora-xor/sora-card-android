package jp.co.soramitsu.oauth.common.interactors.user.api

import kotlinx.coroutines.flow.Flow

interface UserInteractor {

    val resultFlow: Flow<UserOperationResult>

    suspend fun getUserData()

    suspend fun calculateFreeKycAttemptsLeft(): Result<Int>

}