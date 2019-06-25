package org.secfirst.umbrella.data.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.secfirst.umbrella.di.ApiKeyInfo
import retrofit2.Response
import retrofit2.http.*
import javax.inject.Inject

/**
 * Just define possible header for requests.
 */
class ApiHeader @Inject constructor(internal val publicApiHeader: PublicApiHeader) {

    class PublicApiHeader @Inject constructor(@ApiKeyInfo
                                              @Expose
                                              @SerializedName
                                              ("api_key") val apiKey: String)
}

/**
 * Responsible to tracking all API calls.
 */
interface ApiHelper {

    @GET(NetworkEndPoint.FEED_LIST)
    fun getBlogApiCall(): Deferred<BlogResponse>

    @GET(NetworkEndPoint.FEED_LIST)
    @Wrapped
    fun getFeedList(@Query("country") countryCode: String,
                    @Query("sources") sources: String,
                    @Query("since") since: String): Deferred<ResponseBody>

    @GET
    fun getRss(@Url url: String): Deferred<ResponseBody>
}

interface MatrixApiHelper {

    @POST(NetworkEndPoint.MATRIX_REGISTER_USER)
    fun registerUserAsync(@Header(CONTENT_TYPE) content_type: String,
                          @Body req: String): Deferred<RegisterUserResponse>

    @POST(NetworkEndPoint.MATRIX_USER_LOGIN)
    fun loginAsync(@Header(CONTENT_TYPE) content_type: String,
                   @Body req: String): Deferred<RegisterUserResponse>

    @POST(NetworkEndPoint.MATRIX_USER_LOGOUT)
    fun logoutAsync(@Query(ACCESS_TOKEN) access_token: String): Deferred<Response<ResponseBody>>

    @POST(NetworkEndPoint.MATRIX_CREATE_ROOM)
    fun createRoomAsync(@Query(ACCESS_TOKEN) access_token: String,
                        @Body req: String): Deferred<CreatRoomResponse>

    @GET(NetworkEndPoint.MATRIX_JOINED_ROOMS)
    fun retrieveJoinedRoomsAsync(@Query(ACCESS_TOKEN) access_token: String): Deferred<JoinedRoomsResponse>

    @POST(NetworkEndPoint.MATRIX_SEND_MESSAGE)
    fun sendMessageAsync(@Path("room_id") room_id: String,
                         @Query(ACCESS_TOKEN) access_token: String,
                         @Body req: String): Deferred<SendMessageResponse>

    @GET(NetworkEndPoint.MATRIX_ROOM_MESSAGES)
    fun getRoomMessagesAsync(@Path("room_id") room_id: String,
                             @Query(ACCESS_TOKEN) access_token: String,
                             @Query("from") from: String?,
                             @Query("dir") dir: String,
                             @Query("limit") limit: Int): Deferred<RoomMessageResponse>

    @GET(NetworkEndPoint.MATRIX_SYNC)
    fun getUserNewsAsync(@Query(ACCESS_TOKEN) access_token: String,
                         @Query("since") since: String?): Deferred<SyncResponse>

    @Multipart
    @POST(NetworkEndPoint.MATRIX_UPLOAD_FILE)
    fun uploadFileAsync(@Header(CONTENT_TYPE) content_type: String,
                        @Query(ACCESS_TOKEN) access_token: String,
                        @Query("filename") filename: String,
                        @Part file: MultipartBody.Part): Deferred<UploadFileResponse>

    @GET(NetworkEndPoint.MATRIX_DOWNLOAD_FILE)
    fun downloadFileAsync(@Path("serverName") serverName: String,
                          @Path("mediaId") mediaId: String): Deferred<Response<ResponseBody>>


    companion object {
        const val ACCESS_TOKEN = "access_token"
        const val CONTENT_TYPE = "Content-Type"
    }
}



