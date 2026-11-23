package com.beezy.app.data.local.dao

import com.beezy.app.data.local.entity.UserEntity

interface UserDao {
    suspend fun insertUser(user: UserEntity): Long
    suspend fun getUserByUsernameOrEmail(identifier: String): UserEntity?
    suspend fun getUserById(id: String): UserEntity?
    suspend fun updateUserProfile(id: String, displayName: String, avatarIndex: Int): Int
    suspend fun deleteUser(id: String): Int
    suspend fun getAllUsers(): List<UserEntity>
}
