package org.secfirst.umbrella.feature.chat.interactor

import org.secfirst.umbrella.data.network.MatrixApiHelper
import org.secfirst.umbrella.data.network.loginUserRequest
import org.secfirst.umbrella.data.network.registerUserRequest
import org.secfirst.umbrella.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class ChatInteractorImp @Inject constructor(matrixApiHelper: MatrixApiHelper) : BaseInteractorImp(matrixApiHelper), ChatBaseInteractor {

    override suspend fun registerUser(username: String, password: String, email: String) = matrixApiHelper.registerUserAsync("application/json", registerUserRequest(username, password))

    override suspend fun login(username: String, password: String) = matrixApiHelper.loginAsync("application/json", loginUserRequest(username, password))
}