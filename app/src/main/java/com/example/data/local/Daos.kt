package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedRouteDao {
    @Query("SELECT * FROM saved_routes ORDER BY savedAt DESC")
    fun getAllSavedRoutes(): Flow<List<SavedRouteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedRoute(route: SavedRouteEntity)

    @Query("DELETE FROM saved_routes WHERE busId = :busId")
    suspend fun deleteByBusId(busId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_routes WHERE busId = :busId)")
    fun isBusSaved(busId: String): Flow<Boolean>
}

@Dao
interface UserSessionDao {
    @Query("SELECT * FROM user_session WHERE id = 1 LIMIT 1")
    fun getUserSession(): Flow<UserSessionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserSession(session: UserSessionEntity)

    @Query("DELETE FROM user_session WHERE id = 1")
    suspend fun clearSession()
}
