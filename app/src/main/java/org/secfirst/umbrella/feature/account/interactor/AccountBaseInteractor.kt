package org.secfirst.umbrella.feature.account.interactor

import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import org.secfirst.umbrella.data.database.matrix_account.Account
import org.secfirst.umbrella.data.database.reader.FeedLocation
import org.secfirst.umbrella.data.database.reader.FeedSource
import org.secfirst.umbrella.feature.base.interactor.BaseInteractor
import retrofit2.Response

interface AccountBaseInteractor : BaseInteractor {

    suspend fun accessDatabase(userToken: String)

    suspend fun changeDatabaseAccess(userToken: String): Boolean

    suspend fun insertFeedLocation(feedLocation: FeedLocation)

    suspend fun insertAllFeedSources(feedSources: List<FeedSource>)

    suspend fun fetchFeedSources(): List<FeedSource>

    suspend fun fetchFeedLocation(): FeedLocation?

    suspend fun fetchRefreshInterval(): Int

    suspend fun putRefreshInterval(position: Int): Boolean

    fun setMaskApp(value: Boolean): Boolean

    fun isMaskApp(): Boolean

    fun setFakeView(isShowFakeView: Boolean): Boolean

    fun getMaskApp(): Boolean

    suspend fun serializeNewContent(path: String): Boolean

    suspend fun matrixLogout(access_token: String): Deferred<Response<ResponseBody>>

    suspend fun saveAccount(account: Account)

    suspend fun fetchAccount(username: String): Account?

    fun setMatrixUsername(username: String): Boolean

    fun getMatrixUsername(): String
}