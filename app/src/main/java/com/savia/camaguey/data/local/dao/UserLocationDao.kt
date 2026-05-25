package com.savia.camaguey.data.local.dao

import androidx.room.*
import com.savia.camaguey.data.model.UserLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface UserLocationDao {
    @Query("SELECT * FROM user_locations WHERE id = 1 LIMIT 1")
    fun get(): Flow<UserLocation?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: UserLocation)

    @Update
    suspend fun update(location: UserLocation)

    @Query("DELETE FROM user_locations")
    suspend fun clear()
}
