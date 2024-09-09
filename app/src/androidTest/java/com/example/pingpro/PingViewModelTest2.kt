package com.example.pingpro

import androidx.activity.viewModels
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.pingpro.data.PingDatabase
import com.example.pingpro.ui.PingEvent
import com.example.pingpro.ui.PingViewModel


import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PingViewModelTest2 {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    private lateinit var viewModel : PingViewModel
    private lateinit var database: PingDatabase


    @Before
    fun setUp() {

        database = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        PingDatabase::class.java
    ).build()
        viewModel = PingViewModel(database.pingDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun get_targets() {
        Assert.assertEquals(0, viewModel.state.value.targets.size)
        viewModel.onEvent(PingEvent.setTarget("8.8.8.8"))
        Assert.assertEquals("8.8.8.8", viewModel.state.value.target)
        viewModel.onEvent(PingEvent.SavePing)
        Assert.assertEquals(1, viewModel.state.value.targets.size)
    }

    @Test
    fun get_pings() {
    }

    @Test
    fun getState() {

    }

    @Test
    fun pingAttempt() {
        Assert.assertEquals(true, viewModel.pingAttempt("8.8.8.8").is_reachable)
        Assert.assertEquals(true, viewModel.pingAttempt("google.com").is_reachable)
        Assert.assertEquals(true, viewModel.pingAttempt("Amazon.com").is_reachable)
        Assert.assertEquals(false, viewModel.pingAttempt("yahoodotcome").is_reachable)
        Assert.assertEquals(false, viewModel.pingAttempt("amazon..com").is_reachable)
        Assert.assertEquals(false, viewModel.pingAttempt("wwww.google.com").is_reachable)
        Assert.assertEquals(false, viewModel.pingAttempt("w.apple.com.").is_reachable)
    }

    @Test
    fun continuousPing() {
    }

    @Test
    fun onEvent() {
    }
}