package org.secfirst.umbrella.feature.chat.interactor

import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import org.secfirst.umbrella.data.network.RegisterUserResponse
import org.secfirst.umbrella.feature.base.interactor.BaseInteractor
import retrofit2.Response

interface ChatBaseInteractor : BaseInteractor {

    suspend fun registerUser(username: String, password: String, email: String): Deferred<RegisterUserResponse>

    suspend fun login(username: String, password: String): Deferred<RegisterUserResponse>
}