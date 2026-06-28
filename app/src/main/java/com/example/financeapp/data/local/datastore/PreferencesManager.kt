package com.example.financeapp.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.financeapp.domain.model.DataSourceType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "finance_settings")

class PreferencesManager(private val context: Context) {

    companion object {
        private val DATA_SOURCE_KEY = stringPreferencesKey("selected_data_source")
    }

    val selectedDataSource: Flow<DataSourceType> = context.dataStore.data
        .map { preferences ->
            val sourceName = preferences[DATA_SOURCE_KEY] ?: DataSourceType.FIREBASE.name
            DataSourceType.valueOf(sourceName)
        }

    suspend fun saveDataSource(dataSourceType: DataSourceType) {
        context.dataStore.edit { preferences ->
            preferences[DATA_SOURCE_KEY] = dataSourceType.name
        }
    }
}