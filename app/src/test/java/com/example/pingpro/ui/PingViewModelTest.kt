package com.example.pingpro.ui


import com.example.pingpro.data.FakeDao
import org.junit.jupiter.api.Assertions.assertEquals


class PingViewModelTest {


    val dao = FakeDao()
    val viewModel = PingViewModel(dao)

    @org.junit.Test
    fun pingAttempt()  {
        assertEquals(false, viewModel.pingAttempt("8.8.8.8").is_reachable)
        assertEquals(false, viewModel.pingAttempt("google.com").is_reachable)
        assertEquals(false, viewModel.pingAttempt("127.0.0.1").is_reachable)
        assertEquals("Error", viewModel.pingAttempt("yahoodotcome").target)
    }

}