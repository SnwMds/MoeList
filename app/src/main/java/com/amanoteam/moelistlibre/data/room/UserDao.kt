package com.amanoteam.moelistlibre.data.room

import androidx.room.*
import com.amanoteam.moelistlibre.data.model.User

@Dao
interface UserDao {

    @Query("SELECT * FROM user WHERE id LIKE :userId ")
    fun getUserById(userId: Int): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Delete
    fun deleteUser(user: User)

    @Delete
    fun deleteAllUsers(user_list: MutableList<User>)
}