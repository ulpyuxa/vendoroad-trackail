package com.simon.trackail.di

import android.content.Context
import androidx.work.WorkManager
import com.simon.trackail.data.local.PreferenceManager
import com.simon.trackail.data.local.TrackailDatabase
import com.simon.trackail.data.local.dao.ShipmentDao
import com.simon.trackail.data.local.dao.TrackingEventDao
import com.simon.trackail.data.remote.TrackApiService
import com.simon.trackail.data.repository.TrackRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton
import androidx.room.Room

/**
 * Hilt 依赖注入模块
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TrackailDatabase {
        return Room.databaseBuilder(
            context,
            TrackailDatabase::class.java,
            "trackail.db"
        ).build()
    }

    @Provides
    fun provideShipmentDao(db: TrackailDatabase): ShipmentDao = db.shipmentDao()

    @Provides
    fun provideTrackingEventDao(db: TrackailDatabase): TrackingEventDao = db.trackingEventDao()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        val json = Json { 
            ignoreUnknownKeys = true 
            coerceInputValues = true
        }
        return Retrofit.Builder()
            .baseUrl("https://api.17track.net/track/v2/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): TrackApiService {
        return retrofit.create(TrackApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePreferenceManager(@ApplicationContext context: Context): PreferenceManager {
        return PreferenceManager(context)
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
}
