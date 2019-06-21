package org.secfirst.umbrella.feature.chat.presenter

import org.json.JSONObject
import org.secfirst.umbrella.data.database.matrix_account.Account
import org.secfirst.umbrella.data.network.Direction
import org.secfirst.umbrella.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.feature.chat.interactor.ChatBaseInteractor
import org.secfirst.umbrella.feature.chat.view.ChatView
import org.secfirst.umbrella.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.misc.launchSilent
import retrofit2.HttpException
import javax.inject.Inject

class ChatPresenterImp<V : ChatView, I : ChatBaseInteractor> @Inject constructor(interactor: I) : BasePresenterImp<V, I>(interactor = interactor),
        ChatBasePresenter<V, I> {

    override fun submitRegisterUser(username: String, password: String, email: String) {
        interactor?.let {
            launchSilent(uiContext) {
                try {
                    val response = it.registerUser(username, password, email).await()
                    val account = Account(username, password, response.access_token, response.home_server, response.device_id, email, true)
                    it.saveAccount(account)
                    it.setMatrixUsername(username)
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
                    val roomsResponse = it.getJoinedRooms(response.access_token).await()
                    if (account != null) {
                        account.access_token = response.access_token
                        account.isLoggedIn = true
                        account.joined_rooms = roomsResponse.joined_rooms
                    } else account = Account(username, password, response.access_token, response.home_server, response.device_id, "", true, roomsResponse.joined_rooms)
                    it.saveAccount(account)
                    it.setMatrixUsername(username)
                    getView()?.logInSuccess(username, account.joined_rooms)
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
                    val response = it.getRoomMessages(account!!.access_token, room_id, "t14-15_3_0_1_1_1_1_101_1", Direction.FORWARD.dir).await()
                    getView()?.showRoomMessages(response.chunk, it.getMatrixUsername())

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

    companion object {
        const val USER_IN_USE_ERROR = "M_USER_IN_USE"
    }
}

