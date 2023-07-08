package jp.co.soramitsu.oauth.core.engines

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.components.SingletonComponent
import jp.co.soramitsu.oauth.core.engines.activityresult.api.ActivityResult
import jp.co.soramitsu.oauth.core.engines.activityresult.impl.ActivityResultImpl
import jp.co.soramitsu.oauth.core.engines.coroutines.api.CoroutinesStorage
import jp.co.soramitsu.oauth.core.engines.coroutines.impl.CoroutinesStorageImpl
import jp.co.soramitsu.oauth.core.engines.preferences.api.KeyValuePreferences
import jp.co.soramitsu.oauth.core.engines.preferences.impl.KeyValuePreferencesImpl
import jp.co.soramitsu.oauth.core.engines.rest.api.RestClient
import jp.co.soramitsu.oauth.core.engines.rest.impl.RestClientImpl
import jp.co.soramitsu.oauth.core.engines.router.api.ComposeRouter
import jp.co.soramitsu.oauth.core.engines.router.impl.ComposeRouterImpl
import javax.inject.Singleton

@Module
@InstallIn(ActivityRetainedComponent::class)
interface ActivityRetainedEnginesBinderModule {

    @Binds
    @ActivityRetainedScoped
    fun bindActivityResult(
        activityResultImpl: ActivityResultImpl
    ): ActivityResult
}

@Module
@InstallIn(SingletonComponent::class)
interface SingletonEnginesBinderModule {

    @Binds
    @Singleton
    fun bindCoroutinesStorage(
        coroutinesStorageImpl: CoroutinesStorageImpl
    ): CoroutinesStorage

    @Binds
    @Singleton
    fun bindKeyValuePreferences(
        keyValuePreferencesImpl: KeyValuePreferencesImpl
    ): KeyValuePreferences

    @Binds
    @Singleton
    fun bindRestClient(
        restClientImpl: RestClientImpl
    ): RestClient

    @Binds
    @Singleton
    fun bindComposeRouter(
        composeRouterImpl: ComposeRouterImpl
    ): ComposeRouter

}