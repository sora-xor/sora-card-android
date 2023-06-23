package jp.co.soramitsu.oauth.core.engines

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.core.engines.network.SoraCardClientProvider
import jp.co.soramitsu.oauth.core.engines.network.SoraCardClientProviderImpl
import jp.co.soramitsu.oauth.core.engines.network.SoraCardNetworkClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

private const val USER_PREFS_NAME = "sora_card_data_store"

@Module
@InstallIn(SingletonComponent::class)
class EnginesModule {

    @Singleton
    @Provides
    fun provideSoraCardClientProvider(): SoraCardClientProvider = SoraCardClientProviderImpl()

    @Singleton
    @Provides
    fun provideSoraCardNetworkClient(
        provider: SoraCardClientProvider,
        inMemoryRepo: InMemoryRepo,
    ): SoraCardNetworkClient =
        SoraCardNetworkClient(provider = provider, inMemoryRepo = inMemoryRepo)

    @Singleton
    @Provides
    fun providePreferencesDataStore(
        @ApplicationContext appContext: Context
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(SharedPreferencesMigration(appContext, USER_PREFS_NAME)),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { appContext.preferencesDataStoreFile(USER_PREFS_NAME) }
        )
    }

}