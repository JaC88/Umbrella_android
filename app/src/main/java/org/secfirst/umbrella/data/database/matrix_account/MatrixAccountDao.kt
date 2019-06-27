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

    suspend fun getRoom(room_id: String): Room? = withContext(ioContext) {
        SQLite.select()
                .from(Room::class.java)
                .where(Room_Table.room_id.`is`(room_id))
                .querySingle()
    }

    suspend fun saveContact(contact: Contact) {
        withContext(ioContext) {
            modelAdapter<Contact>().save(contact)
        }
    }

//    suspend fun getContact(account: Account, room: Room) : Contact? = withContext(ioContext){
//        SQLite.select(Contact_Table.name)
//                .from(Contact::class.java)
//                .where(Contact_Table.account.`is`(account))
//                .and(Contact_Table.room.`is`(room))
//                .querySingle()
//    }
}