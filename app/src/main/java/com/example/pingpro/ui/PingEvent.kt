package com.example.pingpro.ui

import com.example.pingpro.data.PingDb

sealed interface PingEvent {
    object SavePing: PingEvent
    data class setTarget(val target: String): PingEvent
    object ShowDialog: PingEvent
    object HideDialog: PingEvent
    object ShowOptions: PingEvent
    object HideOptions: PingEvent
    //data class SortPings(val sortType: SortType): PingEvent
    data class DeleteTargets(val target: String): PingEvent
    data class DeleteTask(val ping: PingDb): PingEvent
    object Pause: PingEvent
    object Resume: PingEvent
    data class setDelay(val delay: Long): PingEvent
    data class setViewablePings(val viewablePings: Int): PingEvent

}