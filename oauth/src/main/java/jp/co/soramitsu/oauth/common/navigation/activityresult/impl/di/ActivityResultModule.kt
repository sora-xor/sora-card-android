package jp.co.soramitsu.oauth.common.navigation.activityresult.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.co.soramitsu.oauth.common.navigation.activityresult.api.ActivityResult
import jp.co.soramitsu.oauth.common.navigation.activityresult.impl.ActivityResultImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ActivityResultModule {

    @Binds
    @Singleton
    fun bindSetActivityResult(activityResultImpl: ActivityResultImpl): ActivityResult

}