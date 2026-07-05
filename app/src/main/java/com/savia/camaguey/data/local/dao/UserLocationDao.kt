package com.savia.camaguey.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.savia.camaguey.data.model.UserLocation

@Dao
interface UserLocationDao {
    @Query("SELECT * FROM user_location WHERE id = 1 LIMIT 1")
    suspend fun get(): UserLocation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: UserLocation)

    @Update
    suspend fun update(location: UserLocation)

    @Query("DELETE FROM user_location")
    suspend fun deleteAll()
}
