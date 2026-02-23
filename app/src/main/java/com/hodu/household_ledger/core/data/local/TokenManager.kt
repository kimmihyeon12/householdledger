package com.hodu.household_ledger.core.data.local

import android.content.Context
import android.content.SharedPreferences

object TokenManager {

    private const val PREF_NAME = "auth_prefs"
    private const val KEY_JWT = "jwt_token"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_JWT, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_JWT, null)
    }

    fun clearToken() {
        prefs.edit().remove(KEY_JWT).apply()
    }
}
