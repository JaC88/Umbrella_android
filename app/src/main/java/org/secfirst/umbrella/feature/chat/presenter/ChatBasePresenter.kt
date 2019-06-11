package org.secfirst.umbrella.feature.chat.presenter

import org.secfirst.umbrella.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.feature.chat.interactor.ChatBaseInteractor
import org.secfirst.umbrella.feature.chat.view.ChatView

interface ChatBasePresenter<V : ChatView, I : ChatBaseInteractor> : BasePresenter<V, I> {

    fun submitRegisterUser(username: String, password: String, email: String)

    fun submitLogin(username: String, password: String)
}