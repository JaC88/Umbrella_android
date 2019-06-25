package org.secfirst.umbrella.data.network

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonQualifier
import kotlinx.android.parcel.Parcelize


@Suppress("MatchingDeclarationName")
data class BlogResponse(@Expose
                        @SerializedName("status_code")
                        private var statusCode: String? = null,

                        @Expose
                        @SerializedName("message")
                        private var message: String? = null,

                        @Expose
                        @SerializedName("date")
                        var data: List<Blog>? = null)

@Parcelize
data class FeedItemResponse(
        @SerializedName("title")
        var title: String = "",
        @SerializedName("description")
        var description: String = "",
        @SerializedName("url")
        var url: String = "",
        @field:Json(name = "")
        @SerializedName("updated_at")
        var updatedAt: Long = 0) : Parcelable


//MATRIX
@Parcelize
data class RegisterUserResponse(
        @SerializedName("access_token")
        var access_token: String = "",
        @SerializedName("home_server")
        var home_server: String = "",
        @SerializedName("user_id")
        var user_id: String = "",
        @SerializedName("device_id")
        var device_id: String = ""
) : Parcelable

data class RoomJoinedMembersResponse(@SerializedName("joined")
                                     var joined: Map<String, RoomMember>)

data class RoomMember(@SerializedName("avatar_url")
                      var avatar_url: String = "",
                      @SerializedName("display_name")
                      var display_name: String = "")

data class JoinedRoomsResponse(@SerializedName("joined_rooms")
                               var joined_rooms: MutableList<String> = mutableListOf())

data class SendMessageResponse(@SerializedName("event_id")
                               var event_id: String = "")

data class CreatRoomResponse(@SerializedName("room_id")
                             var room_id: String = "",
                             @SerializedName("room_alias")
                             var room_alias: String = "")

data class UploadFileResponse(@SerializedName("content_uri")
                              var content_uri: String = "")


@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class Wrapped
