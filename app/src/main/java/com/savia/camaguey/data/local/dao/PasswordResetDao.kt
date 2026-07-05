package com.savia.camaguey.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.savia.camaguey.data.model.PasswordReset

@Dao
interface PasswordResetDao {
    @Query("SELECT * FROM password_resets WHERE username = :username AND usado = 0 AND expiraEn > :now ORDER BY creadoEn DESC LIMIT 1")
    suspend fun getValidByUsername(username: String, now: Long = System.currentTimeMillis()): PasswordReset?

    @Query("SELECT * FROM password_resets WHERE codigo = :codigo AND usado = 0 AND expiraEn > :now LIMIT 1")
    suspend fun getValidByCode(codigo: String, now: Long = System.currentTimeMillis()): PasswordReset?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reset: PasswordReset)

    @Query("UPDATE password_resets SET usado = 1 WHERE id = :id")
    suspend fun markUsed(id: Long)

    @Query("DELETE FROM password_resets WHERE expiraEn < :now")
    suspend fun deleteExpired(now: Long = System.currentTimeMillis())

    @Query("DELETE FROM password_resets")
    suspend fun deleteAll()
}
