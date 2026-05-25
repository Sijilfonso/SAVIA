package com.savia.camaguey.data.local.dao

import androidx.room.*
import com.savia.camaguey.data.model.AdminAccount

@Dao
interface AdminAccountDao {
    @Query("SELECT * FROM admin_accounts WHERE username = :username LIMIT 1")
    suspend fun getByUsername(username: String): AdminAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: AdminAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(accounts: List<AdminAccount>)

    @Update
    suspend fun update(account: AdminAccount)

    @Delete
    suspend fun delete(account: AdminAccount)
}
