package com.simon.trackail.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 偏好设置管理器，使用加密的 SharedPreferences 存储敏感信息（如 API Key）。
 */
@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext context: Context
) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "trackail_encrypted_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_17TRACK_API_KEY = "17track_api_key"
    }

    /**
     * 保存 17TRACK API Key。
     * @param apiKey 17TRACK 的 API Key
     */
    fun save17TrackApiKey(apiKey: String) {
        sharedPreferences.edit().putString(KEY_17TRACK_API_KEY, apiKey).apply()
    }

    /**
     * 获取 17TRACK API Key。
     * @return 存储的 API Key，如果不存在则返回空字符串
     */
    fun get17TrackApiKey(): String {
        return sharedPreferences.getString(KEY_17TRACK_API_KEY, "") ?: ""
    }

    /**
     * 检查 17TRACK API Key 是否已配置。
     * @return 如果已配置则返回 true，否则返回 false
     */
    fun is17TrackApiKeyConfigured(): Boolean {
        return get17TrackApiKey().isNotBlank()
    }
}
