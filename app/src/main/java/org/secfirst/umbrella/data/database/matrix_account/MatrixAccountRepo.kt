package org.secfirst.umbrella.data.database.matrix_account

interface MatrixAccountRepo {

    suspend fun insertAccount(account:Account)

    suspend fun deleteAccount(account:Account)

    suspend fun loadAccount(username: String) : Account?

    suspend fun insertRoom(room:Room)
}