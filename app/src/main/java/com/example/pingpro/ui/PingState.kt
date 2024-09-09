package com.example.pingpro.ui

import com.example.pingpro.data.PingDb

data class PingState(
    val targets: List<String> = emptyList(),
    //val targetPings: List<Point> = mutableListOf(Point(0F, 0F)),
    val pings: List<PingDb> = emptyList(),
    val target: String = "",
    //val dnsLookup: String = "",
    //val created_at: String = "",
    //val latency: Double = 0.0,
    //val is_reachable: Boolean = true,
    val addingTargetText: String = "Please enter IP or URL",
    val isAddingTarget: Boolean = false,
    val isOptionsOpen: Boolean = false,
    val isPaused: Boolean = true,
    //val results: String = "",
    val delayTime: Long = 2000,
    val viewablePings: Int = 10,

    )
