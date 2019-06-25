package org.secfirst.umbrella.feature.chat.presenter

import android.content.Context
import android.net.Uri
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import org.secfirst.umbrella.data.database.matrix_account.Account
import org.secfirst.umbrella.data.database.matrix_account.Room
import org.secfirst.umbrella.data.network.Chunk
import org.secfirst.umbrella.data.network.Direction
import org.secfirst.umbrella.data.network.SyncResponse
import org.secfirst.umbrella.data.network.createRoomRequest
import org.secfirst.umbrella.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.feature.chat.interactor.ChatBaseInteractor
import org.secfirst.umbrella.feature.chat.view.ChatView
import org.secfirst.umbrella.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.misc.copyStreamToFile
import org.secfirst.umbrella.misc.launchSilent
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
                    val account = Account(username, password, response.access_token, response.home_server, response.device_id, email, false)
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
                    var sync: SyncResponse
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
                    sync.rooms.invite.forEach {
                        if (!account.joined_rooms.contains(it.key))
                            notifications.add(it.value.invite_state.events[0])
                    }
                    it.saveAccount(account)
                    it.setMatrixUsername(username)
                    getView()?.logInSuccess(username, account.joined_rooms, notifications)
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
                    response.chunk.reversed().forEach { if (it.content.msgtype == "m.text") textMessage.add(it) }
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

    override fun submitSendMessage(room_id: String, body: String) {
        interactor?.let {
            launchSilent(uiContext) {
                try {
                    val account = it.fetchAccount(it.getMatrixUsername())
                    it.sendMessage(room_id, account!!.access_token, body).await()
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
                    println(createRoomRequest(contactName))
                    val roomRequest = it.createRoom(account!!.access_token, createRoomRequest(contactName)).await()
                    it.saveRoom(Room(roomRequest.room_id, roomRequest.room_alias))
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
                val account = it.fetchAccount(it.getMatrixUsername())
                getView()?.showContacts(account!!.joined_rooms)
            }
        }
    }

    override fun submitUploadFile(context: Context) {
        interactor?.let {
            launchSilent(uiContext) {
                try {
//                    val account = it.fetchAccount(it.getMatrixUsername())
//                    val file = File(context.cacheDir, "Checklist.pdf")
//                    println(file.absolutePath)
//                    println(Uri.fromFile(file))
//                    val requestBody = RequestBody.create(MediaType.parse("application/pdf"), file)
//                    val body = MultipartBody.Part.create(requestBody)
//                    val uploadFileResponse = it.uploadFile("application/pdf", account!!.access_token, "prova", body).await()
//                    println("URI: " + uploadFileResponse.content_uri)
                    val response = it.downloadFile("comms.secfirst.org", "VCCvFsFlNPhDtioZFLfHJfIG").await()
                    println(response.message())
                    if (response.isSuccessful) {
                        val inputStream: InputStream = response.body()!!.byteStream()
                        val fileToShare = File(context.cacheDir, "prova.pdf")
                        copyStreamToFile(inputStream, fileToShare)
                        println(fileToShare.absolutePath)
                    }

                } catch (e: Exception) {
                    if (e is HttpException)
                        println("ERROR" + e.response().errorBody()?.string())
                    else println("error" + e.message)
                }
            }
        }
    }

    companion object {
        const val USER_IN_USE_ERROR = "M_USER_IN_USE"
    }
}

