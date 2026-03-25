package com.simon.trackail.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 偏好设置管理器，使用加密的 SharedPreferences 存储敏感信息（如 API Key）。
 * 若加密初始化失败（如部分设备不兼容），则自动降级为普通 SharedPreferences。
 */
@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val KEY_17TRACK_API_KEY = "17track_api_key"
        private const val KEY_SYNC_FREQUENCY = "sync_frequency"
        private const val ENCRYPTED_PREFS_NAME = "trackail_encrypted_prefs"
        private const val PLAIN_PREFS_NAME = "trackail_prefs"
    }

    /**
     * 懒加载 SharedPreferences，优先使用加密版本，失败时降级为明文版本。
     * 延迟初始化可以避免在主线程同步创建加密存储导致的 ANR 或崩溃。
     */
    private val sharedPreferences: SharedPreferences by lazy {
        createEncryptedPrefs() ?: context.getSharedPreferences(PLAIN_PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * 尝试创建加密的 SharedPreferences，捕获所有可能的异常并返回 null。
     */
    private fun createEncryptedPrefs(): SharedPreferences? {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                context,
                ENCRYPTED_PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Throwable) {
            // 加密初始化失败（设备不兼容、keystore 损坏或底层依赖异常），降级为普通存储
            e.printStackTrace()
            null
        }
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
     * 清除 17TRACK API Key。
     */
    fun clear17TrackApiKey() {
        sharedPreferences.edit().remove(KEY_17TRACK_API_KEY).apply()
    }

    /**
     * 检查 17TRACK API Key 是否已配置。
     * @return 如果已配置则返回 true，否则返回 false
     */
    fun is17TrackApiKeyConfigured(): Boolean {
        return get17TrackApiKey().isNotBlank()
    }

    /**
     * 保存同步频率（Int 类型，单位：小时）。
     * @param frequency 同步频率（小时）
     */
    fun saveSyncFrequency(frequency: Int) {
        sharedPreferences.edit().putInt(KEY_SYNC_FREQUENCY, frequency).apply()
    }

    /**
     * 获取同步频率（Int 类型，单位：小时）。
     * @return 存储的同步频率，默认值为 4 小时
     */
    fun getSyncFrequency(): Int {
        return sharedPreferences.getInt(KEY_SYNC_FREQUENCY, 4)
    }

    /**
     * 获取同步间隔（Long 类型，供 WorkManager 使用）。
     * @return 同步间隔小时数，默认 4 小时
     */
    fun getSyncInterval(): Long {
        return sharedPreferences.getInt(KEY_SYNC_FREQUENCY, 4).toLong()
    }

    /**
     * 设置同步间隔（Long 类型，供 SettingsViewModel 调用）。
     * @param hours 同步间隔小时数
     */
    fun setSyncInterval(hours: Long) {
        sharedPreferences.edit().putInt(KEY_SYNC_FREQUENCY, hours.toInt()).apply()
    }
}
