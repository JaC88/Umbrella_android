package org.secfirst.umbrella.feature.chat.presenter

import android.content.Context
import android.os.Environment
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import org.secfirst.umbrella.data.database.checklist.Checklist
import org.secfirst.umbrella.data.database.checklist.Dashboard
import org.secfirst.umbrella.data.database.matrix_account.Account
import org.secfirst.umbrella.data.database.matrix_account.Contact
import org.secfirst.umbrella.data.database.matrix_account.Room
import org.secfirst.umbrella.data.network.Chunk
import org.secfirst.umbrella.data.network.Direction
import org.secfirst.umbrella.data.network.SyncResponse
import org.secfirst.umbrella.data.network.createRoomRequest
import org.secfirst.umbrella.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.feature.chat.interactor.ChatBaseInteractor
import org.secfirst.umbrella.feature.chat.view.ChatView
import org.secfirst.umbrella.misc.*
import org.secfirst.umbrella.misc.AppExecutors.Companion.uiContext
import retrofit2.HttpException
import java.io.File
import java.io.InputStream
import javax.inject.Inject

class ChatPresenterImp<V : ChatView, I : ChatBaseInteractor> @Inject constructor(interactor: I) : BasePresenterImp<V, I>(interactor = interactor),
        ChatBasePresenter<V, I> {

    override fun submitRegisterUser(username: String, password: String, email: String) {
        interactor?.let {
            launchSilent(uiContext) {
                try {
                    val response = it.registerUser(username, password, email).await()
                    val account = Account(username, password, "", response.home_server, response.device_id, email, false)
                    it.saveAccount(account)
                    getView()?.regSuccess(username)
                } catch (e: Exception) {
                    if (e is HttpException) {
                        val error = JSONObject(e.response().errorBody()?.string())
                        if (error.get("errcode") == USER_IN_USE_ERROR)
                            getView()?.showUserRegistrationError()
                        getView()?.regError()
                    }
                }
            }
        }
    }

    override fun submitLogin(username: String, password: String) {
        interactor?.let {
            launchSilent(uiContext) {
                try {
                    val response = it.login(username, password).await()
                    var account = it.fetchAccount(username)
                    val sync: SyncResponse
                    val roomsResponse = it.getJoinedRooms(response.access_token).await()
                    if (account != null) {
                        account.access_token = response.access_token
                        account.isLoggedIn = true
                        account.joined_rooms = roomsResponse.joined_rooms
                        sync = it.getUserNews(account.access_token, null).await()
                        account.next_batch = sync.next_batch
                    } else {
                        sync = it.getUserNews(response.access_token, null).await()
                        account = Account(username, password, response.access_token, response.home_server, response.device_id, "", true, roomsResponse.joined_rooms, sync.next_batch)
                    }
                    val notifications = mutableListOf<Chunk>()
                    sync.rooms.invite.forEach { invite ->
                        val joinRoomResponse = interactor?.joinRoom(account.access_token, invite.key)?.await()
                        invite.value.invite_state.events.forEach {
                            if (it.type == "m.room.canonical_alias")
                                interactor?.saveRoom(Room(joinRoomResponse!!.room_id, it.content.alias))

                            if (!account.joined_rooms.contains(invite.key))
                                account.joined_rooms.add(invite.key)
                        }
                    }
                    it.saveAccount(account)
                    it.setMatrixUsername(username)
                    getView()?.logInSuccess(username, notifications)
                } catch (e: Exception) {
                    if (e is HttpException) {
                        val error = JSONObject(e.response().errorBody()?.string())
                        println(error)
                        getView()?.logInError()
                    }
                }
            }
        }
    }

    override fun submitShowRoomMessages(room_id: String) {
        interactor?.let {
            launchSilent(uiContext) {
                try {
                    val account = it.fetchAccount(it.getMatrixUsername())
                    val response = it.getRoomMessages(account!!.access_token, room_id, null, Direction.BACK.dir, 100).await()
                    val textMessage = mutableListOf<Chunk>()
                    response.chunk.reversed().forEach { if (it.content.msgtype == "m.text" || it.content.msgtype == "m.file") textMessage.add(it) }
                    getView()?.showRoomMessages(textMessage, it.getMatrixUsername())
                } catch (e: Exception) {
                    if (e is HttpException) {
                        val error = JSONObject(e.response().errorBody()?.string())
                        println(error)
                    } else println(e.message)
                }
            }
        }

    }

    override fun submitSendMessage(room_id: String, body: String, uriMCX: String, type: String) {
        interactor?.let {
            launchSilent(uiContext) {
                try {
                    val account = it.fetchAccount(it.getMatrixUsername())
                    it.sendMessage(room_id, account!!.access_token, body, uriMCX, type).await()
                    submitShowRoomMessages(room_id)
                } catch (e: HttpException) {
                    println(e.response().errorBody()?.string())
                }
            }
        }
    }

    override fun submitCreateRoom(contactName: String) {
        interactor?.let {
            launchSilent(uiContext) {
                try {
                    val account = it.fetchAccount(it.getMatrixUsername())
                    val roomRequest = it.createRoom(account!!.access_token, createRoomRequest(contactName + "_" + account.username, contactName)).await()
                    account.joined_rooms.add(roomRequest.room_id)
                    it.saveAccount(account)
                    it.saveRoom(Room(roomRequest.room_id, roomRequest.room_alias))
                    val contact = Contact(0, roomRequest.room_id, account, contactName)
                    println(roomRequest.room_alias)
                    getView()?.updateContacts(contact)
                } catch (e: Exception) {
                    if (e is HttpException)
                        println(e.response().errorBody()?.string())
                    else println(e.message)
                }
            }
        }
    }

    override fun isLoggedIn(): Boolean {
        if (interactor?.getMatrixUsername().isNullOrBlank())
            return false
        return true
    }

    override fun submitLoadContacts() {
        interactor?.let {
            launchSilent(uiContext) {
                val username = it.getMatrixUsername()
                val account = it.fetchAccount(username)
                val contacts = mutableListOf<Contact>()
                account?.joined_rooms?.forEach { room_id ->
                    val room = interactor?.getRoom(room_id)
                    val roomContactName = room?.room_alias_name?.toRoomContactName(username)
                    if (roomContactName != null)
                        contacts.add(Contact(id = 0, room_id = room_id, account = account, name = roomContactName))
//                    val roomMembersResponse = interactor?.getRoomMembers(room_id, account.access_token)?.await()
//                    roomMembersResponse?.joined?.forEach {
//                            contacts.add(Contact(id = 0, room_id = room_id, account = account, name = it.key.toContactName()))
//                    }
                }
                getView()?.showContacts(contacts)
            }
        }
    }

    override fun submitUploadFile(file: File, context: Context) {
        interactor?.let {
            launchSilent(uiContext) {
                try {
                    val account = it.fetchAccount(it.getMatrixUsername())
                    val requestBody = RequestBody.create(MediaType.parse("application/pdf"), file)
                    val body = MultipartBody.Part.create(requestBody)
                    val uploadFileResponse = it.uploadFile("application/pdf", account!!.access_token, file.name, body).await()
                    println(uploadFileResponse.content_uri)
                    getView()?.fileUploadSuccess(uploadFileResponse.content_uri, file.name)
                } catch (e: Exception) {
                    if (e is HttpException)
                        println("ERROR" + e.response().errorBody()?.string())
                    else println("error" + e.message)
                }
            }
        }
    }

    override fun submitDownloadFile(context: Context, uriMCX: String) {
        interactor?.let {
            launchSilent(uiContext) {
                try {
                    val response = it.downloadFile(MATRIX_DOMAIN, uriMCX.toMediaId()).await()
                    println(response.message())
                    if (response.isSuccessful) {
                        val inputStream: InputStream = response.body()!!.byteStream()
                        val title = response.headers()["Content-Disposition"]?.replace("inline; filename=", "") + "_${System.currentTimeMillis() / 1000}"
                        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "$title.pdf")
                        copyStreamToFile(inputStream, file)
                        getView()?.fileDownloadSuccess()
                    }
                } catch (e: Exception) {
                    if (e is HttpException)
                        println("ERROR" + e.response().errorBody()?.string())
                    else println("error" + e.message)
                }
            }
        }
    }

    override fun submitLoadItemToShare() {
        launchSilent(uiContext) {
            interactor?.let {
                val allDashboard = mutableListOf<Dashboard.Item>()
                val checklistInProgress = it.fetchAllChecklistInProgress()
                val inProgressList = dashboardMount(checklistInProgress, "My Checklists")
                allDashboard.addAll(inProgressList)
                getView()?.showItemToShare(allDashboard)
            }
        }
    }


    private suspend fun dashboardMount(itemList: List<Checklist>, title: String): List<Dashboard.Item> {
        val dashboards = mutableListOf<Dashboard.Item>()
        val dashboardTitle = Dashboard.Item(title)
        dashboards.add(dashboardTitle)
        interactor?.let { interactor ->
            itemList.forEach { checklist ->
                val difficultyId = checklist.difficulty?.id
                if (difficultyId != null) {
                    val loadDifficulty = interactor.fetchDifficultyById(difficultyId)
                    val subject = interactor.fetchSubjectById(loadDifficulty?.subject!!.id)
                    val dashboardItem = Dashboard.Item(checklist.progress,
                            subject!!.title,
                            checklist,
                            loadDifficulty,
                            loadDifficulty.index)
                    dashboards.add(dashboardItem)
                }
            }
        }
        return dashboards
    }

    companion object {
        const val USER_IN_USE_ERROR = "M_USER_IN_USE"
    }
}

