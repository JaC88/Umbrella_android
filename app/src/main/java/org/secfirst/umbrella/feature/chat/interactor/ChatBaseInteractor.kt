package org.secfirst.umbrella.feature.chat.interactor

import kotlinx.coroutines.Deferred
import org.secfirst.umbrella.data.database.matrix_account.Account
import org.secfirst.umbrella.data.database.matrix_room.Room
import org.secfirst.umbrella.data.network.*
import org.secfirst.umbrella.feature.base.interactor.BaseInteractor

interface ChatBaseInteractor : BaseInteractor {

    suspend fun registerUser(username: String, password: String, email: String): Deferred<RegisterUserResponse>

    suspend fun login(username: String, password: String): Deferred<RegisterUserResponse>

    suspend fun saveAccount(account: Account)

    suspend fun deleteAccount(account: Account)

    suspend fun fetchAccount(username: String): Account?

    suspend fun saveRoom(room: Room)

    suspend fun sendMessage(room_id: String, access_token: String, body: String): Deferred<SendMessageResponse>

    suspend fun getJoinedRooms(access_token: String): Deferred<JoinedRoomsResponse>

    suspend fun getRoomMessages(access_token: String, room_id: String, from: String, dir: String): Deferred<RoomMessageResponse>

    fun setMatrixUsername(username: String): Boolean

    fun getMatrixUsername(): String
}