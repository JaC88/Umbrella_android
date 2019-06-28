package org.secfirst.umbrella.feature.chat.presenter

import android.content.Context
import org.secfirst.umbrella.data.database.form.ActiveForm
import org.secfirst.umbrella.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.feature.chat.interactor.ChatBaseInteractor
import org.secfirst.umbrella.feature.chat.view.ChatView
import java.io.File

interface ChatBasePresenter<V : ChatView, I : ChatBaseInteractor> : BasePresenter<V, I> {

    fun submitRegisterUser(username: String, password: String, email: String)

    fun submitLogin(username: String, password: String)

    fun submitShowRoomMessages(room_id: String)

    fun submitSendMessage(room_id: String, body: String, uriMCX: String, type: String)

    fun submitCreateRoom(contactName: String)

    fun isLoggedIn(): Boolean

    fun submitLoadContacts()

    fun submitUploadFile(file: File, context: Context)

    fun submitDownloadFile(context: Context, uriMCX: String)

    fun submitLoadItemToShare(type: String)

    fun submitShareForm(activeForm: ActiveForm, context: Context)
}