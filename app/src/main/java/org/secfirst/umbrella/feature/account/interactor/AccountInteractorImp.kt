package org.secfirst.umbrella.feature.account.interactor

import org.secfirst.umbrella.data.database.account.AccountRepo
import org.secfirst.umbrella.data.database.content.ContentRepo
import org.secfirst.umbrella.data.database.content.createDefaultRSS
import org.secfirst.umbrella.data.database.content.createFeedSources
import org.secfirst.umbrella.data.database.matrix_account.Account
import org.secfirst.umbrella.data.database.matrix_account.MatrixAccountRepo
import org.secfirst.umbrella.data.database.reader.FeedLocation
import org.secfirst.umbrella.data.database.reader.FeedSource
import org.secfirst.umbrella.data.disk.TentLoader
import org.secfirst.umbrella.data.network.ApiHelper
import org.secfirst.umbrella.data.network.MatrixApiHelper
import org.secfirst.umbrella.data.preferences.AppPreferenceHelper
import org.secfirst.umbrella.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject
import javax.inject.Named


class AccountInteractorImp @Inject constructor(apiHelper: ApiHelper,
                                               preferenceHelper: AppPreferenceHelper,
                                               contentRepo: ContentRepo,
                                               matrixApiHelper: MatrixApiHelper,
                                               private val matrixAccountRepo: MatrixAccountRepo,
                                               private val accountRepo: AccountRepo,
                                               private val tentLoader : TentLoader)

    : BaseInteractorImp(apiHelper, preferenceHelper, contentRepo, matrixApiHelper), AccountBaseInteractor {

    override suspend fun serializeNewContent(path: String) :Boolean{
        val res: Boolean
        val newContent = tentLoader.serializeContent(path)
        res = if (accountRepo.wipeMainContent()) {
            contentRepo.insertAllLessons(newContent)
            contentRepo.insertFeedSource(createFeedSources())
            contentRepo.insertDefaultRSS(createDefaultRSS())
            true
        } else {
            false
        }
        return res
    }

    override fun getMaskApp() = preferenceHelper.isMaskApp()

    override fun setFakeView(isShowFakeView: Boolean)  = preferenceHelper.setMockView(isShowFakeView)

    override fun setMaskApp(value: Boolean) = preferenceHelper.setMaskApp(value)

    override fun isMaskApp() = preferenceHelper.isMaskApp()

    override suspend fun accessDatabase(userToken: String) = accountRepo.loginDatabase(userToken)

    override suspend fun changeDatabaseAccess(userToken: String) = accountRepo.changeToken(userToken)

    override suspend fun insertFeedLocation(feedLocation: FeedLocation) = accountRepo.saveFeedLocation(feedLocation)

    override suspend fun insertAllFeedSources(feedSources: List<FeedSource>) = accountRepo.saveAllFeedSources(feedSources)

    override suspend fun fetchFeedSources() = accountRepo.getAllFeedSources()

    override suspend fun fetchFeedLocation() = accountRepo.getFeedLocation()

    override suspend fun fetchRefreshInterval() = preferenceHelper.getRefreshInterval()

    override suspend fun putRefreshInterval(position: Int) = preferenceHelper.setRefreshInterval(position)

    override suspend fun matrixLogout(access_token: String) = matrixApiHelper.logoutAsync(access_token)

    override suspend fun saveAccount(account: Account) = matrixAccountRepo.insertAccount(account)

    override suspend fun fetchAccount(username: String) = matrixAccountRepo.loadAccount(username)

    override fun setMatrixUsername(username: String) = preferenceHelper.setMatrixUsername(username)

    override fun getMatrixUsername() = preferenceHelper.getMatrixUsername()
}