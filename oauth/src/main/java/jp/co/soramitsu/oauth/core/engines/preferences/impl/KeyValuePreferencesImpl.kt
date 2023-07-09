package jp.co.soramitsu.oauth.core.engines.preferences.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import jp.co.soramitsu.oauth.core.engines.preferences.api.KeyValuePreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

class KeyValuePreferencesImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
): KeyValuePreferences {

    override val dataFlow: Flow<Preferences> = dataStore.data

    override suspend fun putString(field: String, value: String) {
        dataStore.edit {
            it[stringPreferencesKey(field)] = value
        }
    }

    override suspend fun getString(field: String): String =
        dataStore.data.map {
            it[stringPreferencesKey(field)] ?: ""
        }.first()

    override suspend fun putLong(field: String, value: Long) {
        dataStore.edit {
            it[longPreferencesKey(field)] = value
        }
    }

    override suspend fun getLong(field: String, defaultValue: Long): Long =
        dataStore.data.map {
            it[longPreferencesKey(field)] ?: defaultValue
        }.first()

    override suspend fun putBoolean(field: String, value: Boolean) {
        dataStore.edit {
            it[booleanPreferencesKey(field)] = value
        }
    }

    override suspend fun getBoolean(field: String, defaultValue: Boolean): Boolean =
        dataStore.data.map {
            it[booleanPreferencesKey(field)] ?: defaultValue
        }.first()

    override suspend fun clear(field: String) {
        dataStore.edit {
            it.remove(stringPreferencesKey(field))
        }
    }

    override suspend fun clearAll() {
        dataStore.edit {
            it.clear()
        }
    }
}