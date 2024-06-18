
    package com.bangkit.factha.data.preference

    import android.content.Context
    import androidx.datastore.core.DataStore
    import androidx.datastore.preferences.core.Preferences
    import androidx.datastore.preferences.core.edit
    import androidx.datastore.preferences.core.intPreferencesKey
    import androidx.datastore.preferences.core.stringPreferencesKey
    import androidx.datastore.preferences.preferencesDataStore
    import kotlinx.coroutines.flow.Flow
    import kotlinx.coroutines.flow.map

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

    class UserPreferences(private val dataStore: DataStore<Preferences>) {

        companion object {
            private val TOKEN_KEY = stringPreferencesKey("token")
            private val USER_ID_KEY = stringPreferencesKey("user_id")
            private val NAME = stringPreferencesKey("name")
            private val EMAIL = stringPreferencesKey("email")
            private val PASSWORD = stringPreferencesKey("password")
            private val PREMIUM = intPreferencesKey("premium")
            private val IMAGE_B64 = stringPreferencesKey("image_b64")

            @Volatile
            private var INSTANCE: UserPreferences? = null

            fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
                return INSTANCE ?: synchronized(this) {
                    val instance = UserPreferences(dataStore)
                    INSTANCE = instance
                    instance
                }
            }
        }

        val token: Flow<String?>
            get() = dataStore.data.map { preferences ->
                preferences[TOKEN_KEY]

            }

        val userId: Flow<String?>
            get() = dataStore.data.map { preferences ->
                preferences[USER_ID_KEY]
            }



        suspend fun saveUser(token: String, userId: String) {
            dataStore.edit { preferences ->
                preferences[TOKEN_KEY] = token
                preferences[USER_ID_KEY] = userId
            }
        }

        suspend fun saveUserDetails(name: String, email: String, password: String, premium: Int, imageB64: String){
            dataStore.edit{preferences ->
                preferences[NAME] = name
                preferences[EMAIL] = email
                preferences[PASSWORD] = password
                preferences[PREMIUM] = premium
                preferences[IMAGE_B64] = imageB64
            }
        }

        val userDetails: Flow<UserDetails?>
            get() = dataStore.data.map { preferences ->
                val name = preferences[NAME]
                val email = preferences[EMAIL]
                val password = preferences[PASSWORD]
                val premium = preferences[PREMIUM]
                val imageB64 = preferences[IMAGE_B64]

                UserDetails(name, email, password, premium, imageB64)
            }


        suspend fun clearUserDetails() {
            dataStore.edit { preferences ->
                preferences.remove(TOKEN_KEY)
                preferences.remove(USER_ID_KEY)
                preferences.remove(NAME)
                preferences.remove(EMAIL)
                preferences.remove(PASSWORD)
                preferences.remove(PREMIUM)
                preferences.remove(IMAGE_B64)
            }
        }
    }