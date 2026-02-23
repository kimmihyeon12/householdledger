package com.hodu.household_ledger.core.data.local

import android.content.Context
import android.content.SharedPreferences

object ConsentManager {

    private const val PREF_NAME = "consent_prefs"
    private const val KEY_CONSENTED = "has_consented"
    private const val KEY_MARKETING = "marketing_agreed"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun hasConsented(): Boolean {
        return prefs.getBoolean(KEY_CONSENTED, false)
    }

    fun saveConsent(marketingAgreed: Boolean) {
        prefs.edit()
            .putBoolean(KEY_CONSENTED, true)
            .putBoolean(KEY_MARKETING, marketingAgreed)
            .apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
