package com.example.instagramclone.utlis

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

val USER_NAME = stringPreferencesKey("user_name")

interface UserPref{
   fun getName(): Flow<String>
   suspend fun saveName(name: String)

}



class UserPrefImpl(private val dataStore: DataStore<Preferences>): UserPref {
    override fun getName(): Flow<String> {
        return dataStore.data.catch { emit(emptyPreferences()) }.map {
            it[USER_NAME] ?: ""
        }
    }

    override suspend fun saveName(name: String) {
        dataStore.edit {
            it[USER_NAME] = name
        }
    }
}