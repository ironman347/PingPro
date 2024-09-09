package com.example.pingpro.data

import androidx.room.PrimaryKey

data class FakePingDb (
val id: Int = 0,
val target: String = "",
val dnsLookup: String = "",
val created_at: String = "",
val latency: Float = 0.0F,
val is_reachable: Boolean = false
)
