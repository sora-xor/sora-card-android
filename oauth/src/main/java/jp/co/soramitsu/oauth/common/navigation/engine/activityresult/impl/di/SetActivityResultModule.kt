package jp.co.soramitsu.oauth.common.navigation.engine.activityresult.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import jp.co.soramitsu.oauth.common.navigation.engine.activityresult.api.SetActivityResult
import jp.co.soramitsu.oauth.common.navigation.engine.activityresult.impl.SetActivityResultImpl

@Module
@InstallIn(ActivityRetainedComponent::class)
interface SetActivityResultModule {

    @Binds
    @ActivityRetainedScoped
    fun bindSetActivityResult(setActivityResultImpl: SetActivityResultImpl): SetActivityResult

}