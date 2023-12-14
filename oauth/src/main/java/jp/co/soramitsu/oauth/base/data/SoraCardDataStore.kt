package jp.co.soramitsu.oauth.base.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Singleton
class SoraCardDataStore @Inject constructor(
    @ApplicationContext context: Context,
) {

    private companion object {
        const val USER_PREFS_NAME = "sora_card_data_store"
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = USER_PREFS_NAME,
    )

    private val dataStore = context.dataStore

    suspend fun putString(field: String, value: String) {
        dataStore.edit {
            it[stringPreferencesKey(field)] = value
        }
    }

    suspend fun getString(field: String): String = dataStore.data.map {
        it[stringPreferencesKey(field)] ?: ""
    }.first()

    suspend fun getLong(field: String, defaultValue: Long): Long = dataStore.data.map {
        it[longPreferencesKey(field)] ?: defaultValue
    }.first()

    suspend fun putLong(field: String, value: Long) {
        dataStore.edit {
            it[longPreferencesKey(field)] = value
        }
    }

    suspend fun clear(field: String) {
        dataStore.edit {
            it.remove(stringPreferencesKey(field))
        }
    }

    suspend fun clearAll() {
        dataStore.edit {
            it.clear()
        }
    }
}
