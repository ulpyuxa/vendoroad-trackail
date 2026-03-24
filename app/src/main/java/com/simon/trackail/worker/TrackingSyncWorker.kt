package com.simon.trackail.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.simon.trackail.data.local.PreferenceManager
import com.simon.trackail.data.repository.TrackRepository
import com.simon.trackail.data.remote.model.TrackInfoRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

/**
 * 后台同步物流信息的任务
 */
@HiltWorker
class TrackingSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: TrackRepository,
    private val preferenceManager: PreferenceManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // 1. 获取 API Key
        val apiKey = preferenceManager.get17TrackApiKey()
        if (apiKey.isBlank()) {
            return Result.failure()
        }

        // 2. 获取刷新池中的单号
        val shipmentsToRefresh = repository.getActivePoolShipments()
        if (shipmentsToRefresh.isEmpty()) {
            return Result.success()
        }

        // 3. 批量查询 API (分批次，每批最多 40 个)
        try {
            // 这里为了简化实现，直接取前 40 个
            val requestBatch = shipmentsToRefresh.take(40).map { 
                TrackInfoRequest(number = it.trackingNumber, carrier = it.carrierCode)
            }

            // 调用 repository 里的刷新逻辑
            repository.refreshShipments(apiKey, requestBatch)

            return Result.success()
        }
 catch (e: Exception) {
            e.printStackTrace()
            return Result.retry()
        }
    }
}
