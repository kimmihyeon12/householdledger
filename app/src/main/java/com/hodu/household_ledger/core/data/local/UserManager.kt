package com.hodu.household_ledger.core.data.local

import android.content.Context
import android.content.SharedPreferences

object UserManager {

    private const val PREF_NAME = "user_prefs"
    private const val KEY_ID = "user_id"
    private const val KEY_NICKNAME = "user_nickname"
    private const val KEY_PROFILE_IMAGE_URL = "user_profile_image_url"
    private const val KEY_PROVIDER = "user_provider"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun save(id: Long, nickname: String?, profileImageUrl: String?, provider: String) {
        prefs.edit()
            .putLong(KEY_ID, id)
            .putString(KEY_NICKNAME, nickname)
            .putString(KEY_PROFILE_IMAGE_URL, profileImageUrl)
            .putString(KEY_PROVIDER, provider)
            .apply()
    }

    fun getId(): Long = prefs.getLong(KEY_ID, 0L)

    fun getNickname(): String? = prefs.getString(KEY_NICKNAME, null)

    fun getProfileImageUrl(): String? = prefs.getString(KEY_PROFILE_IMAGE_URL, null)

    fun getProvider(): String? = prefs.getString(KEY_PROVIDER, null)

    fun clear() {
        prefs.edit().clear().apply()
    }
}
