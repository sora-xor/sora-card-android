package jp.co.soramitsu.oauth.core.engines.preferences.api

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface KeyValuePreferences {

    val dataFlow: Flow<Preferences>

    suspend fun putString(field: String, value: String)

    suspend fun getString(field: String): String

    suspend fun getLong(field: String, defaultValue: Long): Long

    suspend fun putLong(field: String, value: Long)

    suspend fun clear(field: String)

    suspend fun clearAll()
}