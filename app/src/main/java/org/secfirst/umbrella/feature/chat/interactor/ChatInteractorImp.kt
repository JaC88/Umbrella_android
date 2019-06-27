package org.secfirst.umbrella.feature.chat.interactor

import okhttp3.MultipartBody
import org.secfirst.umbrella.data.database.checklist.Checklist
import org.secfirst.umbrella.data.database.checklist.ChecklistRepo
import org.secfirst.umbrella.data.database.matrix_account.Account
import org.secfirst.umbrella.data.database.matrix_account.MatrixAccountRepo
import org.secfirst.umbrella.data.database.matrix_account.Room
import org.secfirst.umbrella.data.network.MatrixApiHelper
import org.secfirst.umbrella.data.network.loginUserRequest
import org.secfirst.umbrella.data.network.registerUserRequest
import org.secfirst.umbrella.data.network.sendMessageRequest
import org.secfirst.umbrella.data.preferences.AppPreferenceHelper
import org.secfirst.umbrella.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class ChatInteractorImp @Inject constructor(appPreferenceHelper: AppPreferenceHelper,
                                            matrixApiHelper: MatrixApiHelper,
                                            private val matrixAccountRepo: MatrixAccountRepo,
                                            private val checklistRepo: ChecklistRepo) : BaseInteractorImp(appPreferenceHelper, matrixApiHelper), ChatBaseInteractor {

    override suspend fun registerUser(username: String, password: String, email: String) = matrixApiHelper.registerUserAsync("application/json", registerUserRequest(username, password))

    override suspend fun login(username: String, password: String) = matrixApiHelper.loginAsync("application/json", loginUserRequest(username, password))

    override suspend fun saveRoom(room: Room) = matrixAccountRepo.insertRoom(room)

    override suspend fun getRoom(room_id: String) = matrixAccountRepo.loadRoom(room_id)

    override suspend fun getJoinedRooms(access_token: String) = matrixApiHelper.retrieveJoinedRoomsAsync(access_token)

    override suspend fun saveAccount(account: Account) = matrixAccountRepo.insertAccount(account)

    override suspend fun deleteAccount(account: Account) = matrixAccountRepo.deleteAccount(account)

    override suspend fun fetchAccount(username: String) = matrixAccountRepo.loadAccount(username)

    override suspend fun sendMessage(room_id: String, access_token: String, body: String, url: String, type: String) = matrixApiHelper.sendMessageAsync(room_id, access_token, sendMessageRequest(type, body, url))

    override suspend fun joinRoom(access_token: String, room_id: String) = matrixApiHelper.joinRoomAsync(room_id, access_token)

    override suspend fun getRoomMessages(access_token: String, room_id: String, from: String?, dir: String, limit: Int) = matrixApiHelper.getRoomMessagesAsync(room_id, access_token, from, dir, limit)

    override suspend fun getRoomMembers(room_id: String, access_token: String) = matrixApiHelper.getRoomMembersAsync(room_id, access_token)

    override suspend fun createRoom(access_token: String, body: String) = matrixApiHelper.createRoomAsync(access_token, body)

    override suspend fun getUserNews(access_token: String, since: String?) = matrixApiHelper.getUserNewsAsync(access_token, since)

    override suspend fun uploadFile(content_type: String, access_token: String, filename: String, file: MultipartBody.Part) = matrixApiHelper.uploadFileAsync(content_type, access_token, filename, file)

    override suspend fun downloadFile(serverName: String, mediaId: String) = matrixApiHelper.downloadFileAsync(serverName, mediaId)

    override fun setMatrixUsername(username: String) = preferenceHelper.setMatrixUsername(username)

    override fun getMatrixUsername() = preferenceHelper.getMatrixUsername()

    override suspend fun fetchAllChecklistInProgress(): List<Checklist> = checklistRepo.loadAllChecklistInProgress()

    override suspend fun fetchSubjectById(subjectId: String) = checklistRepo.loadSubjectById(subjectId)

    override suspend fun fetchDifficultyById(difficultyId: String) = checklistRepo.loadDifficultyById(difficultyId)
}