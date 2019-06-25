package org.secfirst.umbrella.data.database.matrix_account

import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.withContext
import org.secfirst.umbrella.misc.AppExecutors.Companion.ioContext

interface MatrixAccountDao {

    suspend fun save(account: Account) {
        withContext(ioContext) {
            modelAdapter<Account>().save(account)
        }
    }

    suspend fun delete(account: Account) {
        withContext(ioContext) {
            modelAdapter<Account>().delete(account)
        }
    }

    suspend fun getAccount(username: String): Account? = withContext(ioContext) {
        SQLite.select()
                .from(Account::class.java)
                .where(Account_Table.username.`is`(username))
                .querySingle()
    }

    suspend fun saveRoom(room: Room) {
        withContext(ioContext) {
            modelAdapter<Room>().save(room)
        }
    }
}