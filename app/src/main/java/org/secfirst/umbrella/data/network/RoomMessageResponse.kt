package org.secfirst.umbrella.data.network

import com.google.gson.annotations.SerializedName


data class RoomMessageResponse(
        @SerializedName("chunk")
        val chunk: List<Chunk>,
        @SerializedName("end")
        val end: String,
        @SerializedName("start")
        val start: String
)

data class Chunk(
        @SerializedName("age")
        val age: Int,
        @SerializedName("content")
        val content: Content,
        @SerializedName("event_id")
        val event_id: String,
        @SerializedName("origin_server_ts")
        val origin_server_ts: Long,
        @SerializedName("room_id")
        val room_id: String,
        @SerializedName("sender")
        val sender: String,
        @SerializedName("state_key")
        val state_key: String,
        @SerializedName("type")
        val type: String,
        @SerializedName("unsigned")
        val unsigned: Unsigned,
        @SerializedName("user_id")
        val user_id: String
)

data class Content(@SerializedName("msgtype")
                   var msgtype: String = "",
                   @SerializedName("body")
                   var body: String = "")

data class Unsigned(
        @SerializedName("age")
        val age: Int
)