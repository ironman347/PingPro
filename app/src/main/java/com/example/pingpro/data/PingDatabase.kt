package com.example.pingpro.data

import androidx.room.Database
import androidx.room.RoomDatabase



@Database(
    entities = [PingDb::class],
    version = 1,
    exportSchema = false
)
abstract class PingDatabase(): RoomDatabase() {
    abstract fun pingDao(): PingDao
}