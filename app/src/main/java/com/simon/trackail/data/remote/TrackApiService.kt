package com.simon.trackail.data.remote

import com.simon.trackail.data.remote.model.*
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

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
     * 获取单号查询详情 (GetTrackInfo)
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
     * 获取运输商代码 (GetCarrierCode)
     * 用于自动识别单号所属的运输商。
     * 每次请求最多支持 40 个单号。
     *
     * @param token API 密钥 (17token)
     * @param requests 识别运输商请求列表
     * @return 响应结果，包含识别出的运输商代码
     */
    @POST("getcarriercode")
    suspend fun getCarrierCode(
        @Header("17token") token: String,
        @Body requests: List<CarrierCodeRequest>
    ): TrackResponse<DataContainer<CarrierResult>>
}
