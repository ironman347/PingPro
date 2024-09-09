package com.example.pingpro.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "pings")
data class PingDb(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val target: String = "",
    val dnsLookup: String = "",
    val created_at: String = "",
    val latency: Float = 0.0F,
    val is_reachable: Boolean = false
)
