package com.example.pingpro.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PingDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(ping: PingDb)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(ping: PingDb)

    @Delete
    suspend fun delete(ping: PingDb)

    @Query("SELECT * from pings WHERE target = :target ORDER BY created_at ASC")
    fun getTargetPings(target: String): Flow<List<PingDb>>

    @Query("DELETE from pings WHERE target = :target")
    fun deleteTargetPings(target: String)

    @Query("SELECT DISTINCT target from pings")
    fun getAllTargets(): Flow<List<String>>

    @Query("SELECT * from pings ORDER BY created_at ASC")
    fun getAllPings(): Flow<List<PingDb>>
}