package ai.achaialabs.helios.heliosApp.data.local.datasource

import ai.achaialabs.helios.heliosApp.data.local.dao.UserDao
import ai.achaialabs.helios.heliosApp.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface AuthLocalDataSource {
    fun getCurrentUser(): Flow<UserEntity?>
    suspend fun getCurrentUserSync(): UserEntity?
    suspend fun saveUser(user: UserEntity)
    suspend fun clearUser()
}

class AuthLocalDataSourceImpl(
    private val userDao: UserDao
) : AuthLocalDataSource {
    override fun getCurrentUser(): Flow<UserEntity?> = userDao.getCurrentUser()
    
    override suspend fun getCurrentUserSync(): UserEntity? = userDao.getCurrentUserSync()

    override suspend fun saveUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    override suspend fun clearUser() {
        userDao.deleteUser()
    }
}
