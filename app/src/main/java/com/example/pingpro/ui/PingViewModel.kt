package com.example.pingpro.ui

import android.icu.text.DateFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pingpro.data.PingDao
import com.example.pingpro.data.PingDb
import com.stealthcopter.networktools.Ping
import com.stealthcopter.networktools.ping.PingResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DateFormat.MEDIUM

class PingViewModel (
    private val dao: PingDao
): ViewModel() {
    private val _state = MutableStateFlow(PingState())
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val _targets = _state.flatMapLatest { dao.getAllTargets()}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val _pings = _state.flatMapLatest { dao.getAllPings()}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    val state = combine(_state, _targets, _pings) { state, targets, pings ->
        state.copy(
            targets = targets,
            pings = pings
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PingState())

    fun pingAttempt(host: String): PingDb {
        try {
                val ping: PingResult =
                    Ping.onAddress(host).setTimeOutMillis(1000).doPing()
                    val newTarget = PingDb(
                        target = ping.ia.hostAddress,
                        dnsLookup = ping.ia.hostName,
                        created_at = DateFormat.getTimeInstance(MEDIUM, java.util.Locale.getDefault()).format(System.currentTimeMillis()).toString(),
                        latency = ping.timeTaken,
                        is_reachable = ping.isReachable(),
                    )
                    return newTarget

        } catch (e: Exception) {
            //Log.e("ping error", e.toString())
            return PingDb(
                target = "Error"
            )
        }
    }

    fun continuousPing() {
        viewModelScope.launch(Dispatchers.Default) {
            while (!state.value.isPaused) {
                if (state.value.targets.isEmpty()) {
                    _state.update {
                        it.copy(
                            isPaused = true
                        )
                    }
                }
                    for (host in state.value.targets) {
                        dao.insert(pingAttempt(host))
                    }
                    delay(state.value.delayTime)
                }
            }
        }

    fun onEvent(event: PingEvent) {
        when(event) {
            is PingEvent.DeleteTask -> {
                viewModelScope.launch(Dispatchers.Default) { dao.deleteTargetPings(event.ping.target) }
            }

            is PingEvent.DeleteTargets -> {
                viewModelScope.launch(Dispatchers.Default) {
                    dao.deleteTargetPings(event.target)
                    //Log.d("delete", "targets ${state.value.targets}")
                }
            }
            
            PingEvent.HideDialog -> {
                _state.update { it.copy(
                    isAddingTarget = false
                ) }
            }

            PingEvent.SavePing -> {
                viewModelScope.launch(Dispatchers.Default) {
                    val host = state.value.target
                    val newHost = pingAttempt(host)

                    if (newHost.target.equals("Error")) {
                        dao.deleteTargetPings("Error")
                        _state.update {
                            it.copy(
                                addingTargetText = "Please enter valid target",
                                target = "",
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                addingTargetText = "Please enter an IP or URL",
                                isAddingTarget = false,
                                target = "",
                            )
                        }
                        dao.insert(newHost)
                    }
                }
            }

            is PingEvent.setTarget -> {
                _state.update { it.copy(
                    target = event.target
                ) }
            }
            PingEvent.ShowDialog -> {
                _state.update {  it.copy(
                    isAddingTarget = true,
                    addingTargetText = "Please enter an IP or URL",
                    isPaused = true
                ) }
            }
            PingEvent.ShowOptions -> {
                _state.update {  it.copy(
                    isOptionsOpen = true,
                    isPaused = true
                ) }
            }
            PingEvent.HideOptions -> {
                _state.update {  it.copy(
                    isOptionsOpen = false,
                ) }
            }
            PingEvent.Pause -> {
                _state.update { it.copy(
                    isPaused = true
                )
                }
                //Log.d("isPaused", "${state.value.isPaused}")
            }
            PingEvent.Resume -> {
                _state.update { it.copy(
                    isPaused = false
                ) }
                continuousPing()
//                /Log.d("isResumed", "Pause = ${state.value.isPaused}")
            }

            is PingEvent.setDelay -> {
                _state.update {
                    it.copy(
                        delayTime = event.delay
                    )
                }
            }
            is PingEvent.setViewablePings -> {
                _state.update {
                    it.copy(
                        viewablePings = event.viewablePings
                    )
                }
            }
        }
    }
}