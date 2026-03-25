package com.simon.trackail.data.remote

import com.simon.trackail.data.remote.model.*
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import kotlinx.serialization.json.JsonElement

/**
 * 17TRACK V2.4 API 接口服务
 * 
 * 基础 URL: https://api.17track.net/track/v2/
 * 认证请求头: 17token: {AccessKey}
 */
interface TrackApiService {

    /**
     * 注册单号 (Register)
     * 在查询单号之前，必须先将其注册到系统中。
     * 每次请求最多支持 40 个单号。
     *
     * @param token API 密钥 (17token)
     * @param requests 注册请求列表
     * @return 响应结果，包含成功受理和拒绝受理的项目
     */
    @POST("register")
    suspend fun register(
        @Header("17token") token: String,
        @Body requests: List<RegisterRequest>
    ): TrackResponse<DataContainer<RegisterResult>>

    /**
     * 获取单号查询详情 (GetTrackInfo - 缓存/异步)
     * 用于主动拉取已注册单号的最新物流轨迹。
     * 每次请求最多支持 40 个单号。
     *
     * @param token API 密钥 (17token)
     * @param requests 查询详情请求列表
     * @return 响应结果，包含详细轨迹信息
     */
    @POST("gettrackinfo")
    suspend fun getTrackInfo(
        @Header("17token") token: String,
        @Body requests: List<TrackInfoRequest>
    ): TrackResponse<DataContainer<TrackInfoResult>>

    /**
     * 实时查询物流轨迹 (GetRealTimeTrackInfo - 实时/同步)
     * 强制立刻向承运商发起查询，获取绝不包含缓存的最新数据。
     * 每次请求最多支持 40 个单号，单次调用会比 gettrackinfo 耗时更长，且可能产生额外计费。
     *
     * @param token API 密钥 (17token)
     * @param requests 查询详情请求列表
     * @return 响应结果，包含详细轨迹信息
     */
    @POST("getRealTimeTrackInfo")
    suspend fun getRealTimeTrackInfo(
        @Header("17token") token: String,
        @Body requests: List<TrackInfoRequest>
    ): TrackResponse<DataContainer<TrackInfoResult>>

    /**
     * 用于验证 API Key 是否有效的独立接口
     * 使用 getquota 接口，不需要特定参数，最适合用来测试 Key 的有效性
     */
    @POST("https://api.17track.net/track/v2.4/getquota")
    suspend fun validateToken(
        @Header("17token") token: String,
        @Body emptyPayload: List<String> = emptyList()
    ): TrackResponse<JsonElement>
}
