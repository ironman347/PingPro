package com.example.pingpro.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeDao : PingDao {

    private val pings = mutableListOf<PingDb>()

    override suspend fun insert(ping: PingDb) {
        pings.add(ping)
    }

    override suspend fun update(ping: PingDb) {

    }

    override suspend fun delete(ping: PingDb) {

    }

    override fun getTargetPings(target: String): Flow<List<PingDb>> {
        return flowOf(pings.filter { it.target == target })
    }

    override fun deleteTargetPings(target: String) {
        pings.removeIf { it.target == target }
    }

    override fun getAllTargets(): Flow<List<String>> {
        return flowOf(pings.map { it.target })
    }

    override fun getAllPings(): Flow<List<PingDb>> {
        return flowOf(pings)
    }
}