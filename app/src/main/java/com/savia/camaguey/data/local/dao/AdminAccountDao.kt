package com.savia.camaguey.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.savia.camaguey.data.model.AdminAccount

@Dao
interface AdminAccountDao {
    @Query("SELECT * FROM admin_accounts WHERE username = :username LIMIT 1")
    suspend fun getByUsername(username: String): AdminAccount?

    @Query("SELECT * FROM admin_accounts WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): AdminAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(admin: AdminAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(admins: List<AdminAccount>)

    @Query("UPDATE admin_accounts SET passwordHash = :newHash WHERE id = :id")
    suspend fun updatePassword(id: String, newHash: String)

    @Query("DELETE FROM admin_accounts")
    suspend fun deleteAll()
}
