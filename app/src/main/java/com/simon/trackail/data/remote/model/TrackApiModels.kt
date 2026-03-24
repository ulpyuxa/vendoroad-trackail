package com.simon.trackail.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 17TRACK API 统一响应封装
 * @param code 响应状态码，0 表示成功
 * @param data 响应数据体
 * @param msg 错误消息描述
 */
@Serializable
data class TrackResponse<T>(
    @SerialName("code") val code: Int,
    @SerialName("data") val data: T? = null,
    @SerialName("msg") val msg: String? = null
)

/**
 * API 数据容器，包含成功接受和拒绝的单号
 * @param accepted 成功受理的单号列表
 * @param rejected 拒绝受理的单号列表及其原因
 */
@Serializable
data class DataContainer<T>(
    @SerialName("accepted") val accepted: List<T> = emptyList(),
    @SerialName("rejected") val rejected: List<RejectedItem> = emptyList()
)

/**
 * 注册单号请求
 * @param number 快递单号
 * @param carrier 运输商代码（可选）
 * @param param 附加参数（可选，如邮编、手机号等）
 * @param tag 用户自定义标签（可选）
 * @param lang 轨迹翻译语言（可选）
 */
@Serializable
data class RegisterRequest(
    @SerialName("number") val number: String,
    @SerialName("carrier") val carrier: Int? = null,
    @SerialName("param") val param: String? = null,
    @SerialName("tag") val tag: String? = null,
    @SerialName("lang") val lang: String? = null
)

/**
 * 获取单号信息请求
 * @param number 快递单号
 * @param carrier 运输商代码（可选）
 */
@Serializable
data class TrackInfoRequest(
    @SerialName("number") val number: String,
    @SerialName("carrier") val carrier: Int? = null
)

/**
 * 获取运输商代码请求
 * @param number 快递单号
 */
@Serializable
data class CarrierCodeRequest(
    @SerialName("number") val number: String
)

/**
 * 拒绝受理的项目信息
 */
@Serializable
data class RejectedItem(
    @SerialName("number") val number: String,
    @SerialName("error") val error: ErrorInfo
)

/**
 * 错误详细信息
 */
@Serializable
data class ErrorInfo(
    @SerialName("code") val code: Int,
    @SerialName("message") val message: String
)

/**
 * 注册成功的单号信息
 */
@Serializable
data class RegisterResult(
    @SerialName("number") val number: String,
    @SerialName("carrier") val carrier: Int
)

/**
 * 运输商识别结果
 */
@Serializable
data class CarrierResult(
    @SerialName("number") val number: String,
    @SerialName("carrier") val carrier: Int
)

/**
 * 单号详细轨迹数据
 */
@Serializable
data class TrackInfoResult(
    @SerialName("number") val number: String,
    @SerialName("carrier") val carrier: Int,
    @SerialName("track") val track: TrackDetail
)

/**
 * 轨迹详细信息封装
 * @param status 物流主状态 (10:未查询到, 20:运输中, 30:到达待取, 40:已签收, 50:异常)
 * @param subStatus 物流子状态
 * @param lastEvent 最近一次更新的事件描述
 * @param lastUpdate 最近一次更新的时间
 * @param providerTips 运输商提供的额外提示
 * @param origin 发件地轨迹
 * @param destination 目的地轨迹
 */
@Serializable
data class TrackDetail(
    @SerialName("status") val status: Int,
    @SerialName("sub_status") val subStatus: Int? = null,
    @SerialName("last_event") val lastEvent: String? = null,
    @SerialName("last_update") val lastUpdate: String? = null,
    @SerialName("provider_tips") val providerTips: String? = null,
    @SerialName("z0") val origin: TrackTimeline? = null,
    @SerialName("z1") val destination: TrackTimeline? = null
)

/**
 * 轨迹时间线数据
 * @param countryCode 国家代码 (ISO 3166-1 alpha-2)
 * @param location 城市/省份
 * @param zipCode 邮编
 * @param events 轨迹事件列表
 */
@Serializable
data class TrackTimeline(
    @SerialName("c") val countryCode: String? = null,
    @SerialName("p") val location: String? = null,
    @SerialName("z") val zipCode: String? = null,
    @SerialName("a") val events: List<TrackEvent> = emptyList()
)

/**
 * 具体的轨迹事件
 * @param time 事件时间 (YYYY-MM-DD HH:mm)
 * @param originalDescription 原始轨迹描述
 * @param location 地点
 * @param translatedDescription 翻译后的描述
 */
@Serializable
data class TrackEvent(
    @SerialName("t") val time: String,
    @SerialName("z") val originalDescription: String,
    @SerialName("c") val location: String? = null,
    @SerialName("d") val translatedDescription: String? = null
)
