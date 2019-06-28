package org.secfirst.umbrella.feature.chat.interactor

import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.secfirst.umbrella.data.database.checklist.Checklist
import org.secfirst.umbrella.data.database.difficulty.Difficulty
import org.secfirst.umbrella.data.database.form.ActiveForm
import org.secfirst.umbrella.data.database.form.Form
import org.secfirst.umbrella.data.database.lesson.Subject
import org.secfirst.umbrella.data.database.matrix_account.Account
import org.secfirst.umbrella.data.database.matrix_account.Room
import org.secfirst.umbrella.data.network.*
import org.secfirst.umbrella.feature.base.interactor.BaseInteractor
import retrofit2.Response

interface ChatBaseInteractor : BaseInteractor {

    suspend fun registerUser(username: String, password: String, email: String): Deferred<RegisterUserResponse>

    suspend fun login(username: String, password: String): Deferred<RegisterUserResponse>

    suspend fun saveAccount(account: Account)

    suspend fun deleteAccount(account: Account)

    suspend fun fetchAccount(username: String): Account?

    suspend fun saveRoom(room: Room)

    suspend fun getRoom(room_id: String): Room?

    suspend fun createRoom(access_token: String, body: String): Deferred<CreateRoomResponse>

    suspend fun sendMessage(room_id: String, access_token: String, body: String, url: String, type: String): Deferred<SendMessageResponse>

    suspend fun getJoinedRooms(access_token: String): Deferred<JoinedRoomsResponse>

    suspend fun getRoomMembers(room_id: String, access_token: String): Deferred<RoomJoinedMembersResponse>

    suspend fun getRoomMessages(access_token: String, room_id: String, from: String?, dir: String, limit: Int): Deferred<RoomMessageResponse>

    suspend fun joinRoom(access_token: String, room_id: String): Deferred<CreateRoomResponse>

    suspend fun getUserNews(access_token: String, since: String?): Deferred<SyncResponse>

    suspend fun uploadFile(content_type: String, access_token: String, filename: String, file: MultipartBody.Part): Deferred<UploadFileResponse>

    suspend fun downloadFile(serverName: String, mediaId: String): Deferred<Response<ResponseBody>>

    suspend fun fetchAllChecklistInProgress(): List<Checklist>

    suspend fun fetchSubjectById(subjectId: String): Subject?

    suspend fun fetchDifficultyById(difficultyId: String): Difficulty?

    suspend fun fetchActiveForms(): List<ActiveForm>

    suspend fun fetchModalForms(): List<Form>

    fun setMatrixUsername(username: String): Boolean

    fun getMatrixUsername(): String
}