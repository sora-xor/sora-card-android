package jp.co.soramitsu.oauth.common.navigation.engine.activityresult.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import jp.co.soramitsu.oauth.common.navigation.engine.activityresult.api.SetActivityResult
import jp.co.soramitsu.oauth.common.navigation.engine.activityresult.impl.SetActivityResultImpl

@Module
@InstallIn(SingletonComponent::class)
interface SetActivityResultModule {

    @Binds
    @Singleton
    fun bindSetActivityResult(setActivityResultImpl: SetActivityResultImpl): SetActivityResult
}
