package org.secfirst.umbrella.feature.chat.interactor

import org.secfirst.umbrella.data.database.matrix_account.Account
import org.secfirst.umbrella.data.database.matrix_account.MatrixAccountRepo
import org.secfirst.umbrella.data.database.matrix_room.Room
import org.secfirst.umbrella.data.network.*
import org.secfirst.umbrella.data.preferences.AppPreferenceHelper
import org.secfirst.umbrella.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class ChatInteractorImp @Inject constructor(appPreferenceHelper: AppPreferenceHelper,
                                            matrixApiHelper: MatrixApiHelper,
                                            private val matrixAccountRepo: MatrixAccountRepo) : BaseInteractorImp(appPreferenceHelper, matrixApiHelper), ChatBaseInteractor {

    override suspend fun registerUser(username: String, password: String, email: String) = matrixApiHelper.registerUserAsync("application/json", registerUserRequest(username, password))

    override suspend fun login(username: String, password: String) = matrixApiHelper.loginAsync("application/json", loginUserRequest(username, password))

    override suspend fun saveRoom(room: Room) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getJoinedRooms(access_token: String) = matrixApiHelper.retrieveJoinedRoomsAsync(access_token)

    override suspend fun saveAccount(account: Account) = matrixAccountRepo.insertAccount(account)

    override suspend fun deleteAccount(account: Account) = matrixAccountRepo.deleteAccount(account)

    override suspend fun fetchAccount(username: String) = matrixAccountRepo.loadAccount(username)

    override suspend fun sendMessage(room_id: String, access_token: String, body: String) = matrixApiHelper.sendMessageAsync(room_id, access_token, sendMessageRequest("m.text", body))

    override suspend fun getRoomMessages(access_token: String, room_id: String, from: String, dir: String) = matrixApiHelper.getRoomMessagesAsync(room_id, access_token, from, dir)

    override fun setMatrixUsername(username: String) = preferenceHelper.setMatrixUsername(username)

    override fun getMatrixUsername() = preferenceHelper.getMatrixUsername()
}