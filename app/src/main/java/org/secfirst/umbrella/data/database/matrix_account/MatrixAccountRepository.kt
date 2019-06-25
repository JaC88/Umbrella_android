package org.secfirst.umbrella.data.database.matrix_account

import javax.inject.Inject

class MatrixAccountRepository @Inject constructor(private val matrixAccountDao: MatrixAccountDao) : MatrixAccountRepo {

    override suspend fun insertAccount(account: Account) = matrixAccountDao.save(account)

    override suspend fun deleteAccount(account: Account) = matrixAccountDao.delete(account)

    override suspend fun loadAccount(username: String) = matrixAccountDao.getAccount(username)

    override suspend fun insertRoom(room: Room) = matrixAccountDao.saveRoom(room)
}