package jp.co.soramitsu.oauth.common.navigation.engine.activityresult.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.co.soramitsu.oauth.common.domain.CurrentActivityRetriever
import jp.co.soramitsu.oauth.common.navigation.engine.activityresult.api.SetActivityResult
import jp.co.soramitsu.oauth.common.data.CurrentActivityRetrieverImpl
import jp.co.soramitsu.oauth.common.navigation.engine.activityresult.impl.SetActivityResultImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface SetActivityResultModule {

    @Binds
    @Singleton
    fun bindSetActivityResult(setActivityResultImpl: SetActivityResultImpl): SetActivityResult

}