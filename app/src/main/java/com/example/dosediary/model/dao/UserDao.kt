package com.example.dosediary.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.dosediary.model.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Upsert
    suspend fun upsertUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    //delete user by id
    @Query("DELETE FROM User WHERE id = :id")
    suspend fun deleteUserById(id: Int)

    @Query("SELECT * FROM User WHERE id = :id")
    fun getUserById(id: Int): Flow<User>

    @Query("UPDATE User SET firstName = :firstName, lastname = :lastName, email = :email, password = :password WHERE id = :id")
    suspend fun updateUser(id: Int, firstName: String, lastName: String, email: String, password: String)

    @Query("SELECT * FROM User WHERE firstName = :firstName AND lastname = :lastName")
    fun getUserByFullName(firstName: String, lastName: String): Flow<User>

    @Query("SELECT * FROM User WHERE email = :tryEmail AND password = :tryPassword LIMIT 1")
    fun validateEmailPassword(tryEmail: String, tryPassword:String): Flow<User?>
    @Query("SELECT * FROM User WHERE email = :tryEmail")
    fun verifyUserExist(tryEmail:String): Flow<User?>
}