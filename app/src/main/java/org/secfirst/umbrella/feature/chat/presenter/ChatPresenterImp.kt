package org.secfirst.umbrella.feature.chat.presenter

import org.json.JSONObject
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
                    println(response.user_id)
                } catch (e: Exception) {
                    if (e is HttpException) {
                        val error = JSONObject(e.response().errorBody()?.string())
                        if (error.get("errcode") == USER_IN_USE_ERROR)
                            getView()?.showUserRegistrationError()
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
                    println(response.user_id)
                } catch (e: Exception) {
                    if (e is HttpException) {
                        val error = JSONObject(e.response().errorBody()?.string())
                        println(error)
                    }
                }
            }
        }
    }

    companion object {
        const val USER_IN_USE_ERROR = "M_USER_IN_USE"
    }
}

