package org.secfirst.umbrella.feature.chat.interactor

import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.secfirst.umbrella.data.database.matrix_account.Account
import org.secfirst.umbrella.data.database.matrix_account.Room
import org.secfirst.umbrella.data.network.*
import org.secfirst.umbrella.feature.base.interactor.BaseInteractor
import retrofit2.Response
import java.io.File

interface ChatBaseInteractor : BaseInteractor {

    suspend fun registerUser(username: String, password: String, email: String): Deferred<RegisterUserResponse>

    suspend fun login(username: String, password: String): Deferred<RegisterUserResponse>

    suspend fun saveAccount(account: Account)

    suspend fun deleteAccount(account: Account)

    suspend fun fetchAccount(username: String): Account?

    suspend fun saveRoom(room: Room)

    suspend fun createRoom(access_token: String, body: String): Deferred<CreatRoomResponse>

    suspend fun sendMessage(room_id: String, access_token: String, body: String): Deferred<SendMessageResponse>

    suspend fun getJoinedRooms(access_token: String): Deferred<JoinedRoomsResponse>

    suspend fun getRoomMessages(access_token: String, room_id: String, from: String?, dir: String, limit: Int): Deferred<RoomMessageResponse>

    suspend fun getUserNews(access_token: String, since: String?): Deferred<SyncResponse>

    suspend fun uploadFile(content_type: String, access_token: String, filename: String, file: MultipartBody.Part) : Deferred<UploadFileResponse>

    suspend fun downloadFile(serverName: String, mediaId: String): Deferred<Response<ResponseBody>>

    fun setMatrixUsername(username: String): Boolean

    fun getMatrixUsername(): String
}