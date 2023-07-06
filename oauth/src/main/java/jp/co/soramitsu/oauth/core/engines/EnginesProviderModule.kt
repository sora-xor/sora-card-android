package jp.co.soramitsu.oauth.core.engines

import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.DialogNavigator
import com.google.accompanist.navigation.animation.AnimatedComposeNavigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import jp.co.soramitsu.oauth.BuildConfig
import jp.co.soramitsu.oauth.core.engines.timer.Timer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import javax.inject.Singleton

private const val USER_PREFS_NAME = "sora_card_data_store"

private const val NETWORK_TIMEOUT = 10_000L

@Module
@InstallIn(SingletonComponent::class)
@OptIn(
    ExperimentalSerializationApi::class,
    ExperimentalAnimationApi::class
)
class EnginesProviderModule {

    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext appContext: Context
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(SharedPreferencesMigration(appContext, USER_PREFS_NAME)),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { appContext.preferencesDataStoreFile(USER_PREFS_NAME) }
        )

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient =
        HttpClient(OkHttp) {
            if (BuildConfig.DEBUG) {
                install(Logging) {
                    level = LogLevel.ALL
                    logger = Logger.SIMPLE
                }
            }
            expectSuccess = true
            install(ContentNegotiation) {
                json(
                    contentType = ContentType.Any,
                    json = Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                        explicitNulls = false
                    }
                )
            }
            install(HttpTimeout) {
                requestTimeoutMillis = NETWORK_TIMEOUT
                connectTimeoutMillis = NETWORK_TIMEOUT
                socketTimeoutMillis = NETWORK_TIMEOUT
            }
        }

    @Provides
    @Singleton
    fun provideNavHostController(
        @ApplicationContext context: Context
    ): NavHostController =
        NavHostController(context).apply {
            navigatorProvider.addNavigator(AnimatedComposeNavigator())
            navigatorProvider.addNavigator(ComposeNavigator())
            navigatorProvider.addNavigator(DialogNavigator())
        }

    @Provides
    @Singleton
    fun provideTimer() =
        Timer()

}