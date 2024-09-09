package com.example.pingpro.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.extensions.formatToSinglePrecision
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.pingpro.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen (
    state: PingState,
    onEvent: (PingEvent) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                title = {
                    Text("PingPro")
                },
                 actions = {
                     IconButton(onClick = {
                         onEvent(PingEvent.ShowOptions)
                     }) {
                         Icon(
                             imageVector = Icons.Default.Settings,
                             contentDescription = "Options Menu"
                         )
                     }
                     IconButton(onClick = {
                         onEvent(PingEvent.ShowDialog)
                     }) {
                         Icon(
                             imageVector = Icons.Default.Add,
                             contentDescription = "Add Target"
                         )
                     }
                     IconButton(onClick = {
                         if(state.isPaused){
                             onEvent(PingEvent.Resume)
                         } else {
                             onEvent(PingEvent.Pause)
                         }
                     }) {
                         if(!state.isPaused) {
                             Image (
                                 painter = painterResource(id = R.drawable.baseline_pause_circle_24),
                                 contentDescription = "Pause Pinging",
                                 alignment = Alignment.Center,
                                 modifier = Modifier.scale(1.2f)
                             )
                         } else {
                             Image(
                                 painter = painterResource(id = R.drawable.baseline_play_circle_24),
                                 contentDescription = "Start Pinging",
                                 alignment = Alignment.Center,
                                 modifier = Modifier.scale(1.2f)
                             )
                         }
                     }
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
//        floatingActionButton = {
//            FloatingActionButton(onClick = {
//                onEvent(PingEvent.ShowDialog)
//            }) {
//                Icon(
//                    imageVector = Icons.Default.Add,
//                    contentDescription = "Add Task"
//                )
//            }
//        },
        modifier = Modifier
            .fillMaxSize()
    ) { padding ->
        if(state.isAddingTarget) {
            AddPingDialog(state = state, onEvent = onEvent)
        }
        if(state.isOptionsOpen) {
            OptionsDialog(state = state, onEvent = onEvent)
        }
        LazyColumn (
            contentPadding = padding,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            reverseLayout = true
        ){
            items(state.targets) { target ->
                PingCard(state = state, target= target, onEvent = onEvent)
            }
        }
    }
}


@Composable
fun PingCard(
    state: PingState,
    target: String,
    onEvent: (PingEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var hostName: String = ""
    if (state.pings.filter { it.target == target }.isNotEmpty()) {
        hostName = " / " + state.pings.filter { it.target == target }.first().dnsLookup
    }
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.tertiaryContainer),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            Column(
                modifier = modifier
            ) {
                IconButton(
                    onClick = {
                        onEvent(PingEvent.DeleteTargets(target = target))
                    },
                    modifier = modifier
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Target"
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = modifier,
                        text = target + hostName,
                        fontStyle = MaterialTheme.typography.displayMedium.fontStyle,
                        fontSize = 20.sp
                    )
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            LineChartCard(
                state,
                target,
                modifier = modifier
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LineChartCard(
    state: PingState,
    target: String,
    steps: Int = 5,
    stepSize: Dp = 30.dp,
    modifier: Modifier = Modifier
) {

    var pointsData: List<Point> = graphData(state, target)

    var iterator = 0
    var iChangeTest = 0
    val xAxisData = AxisData.Builder()
        .axisStepSize(stepSize)
        .backgroundColor(MaterialTheme.colorScheme.secondaryContainer)
        .steps(pointsData.size - 1)
        .labelData { i ->
            if (iChangeTest != i && iterator < pointsData.size-1) {
                iterator++
                iChangeTest = i
            }
            state.pings.find {it.id == pointsData[iterator].x.toInt()}?.created_at.toString()
        }
        .axisLabelFontSize(10.sp)
        .axisLabelColor(Color.Black)
        .labelAndAxisLinePadding(20.dp)
        .build()

    val yAxisData = AxisData.Builder()
        .steps(steps)
        .backgroundColor(MaterialTheme.colorScheme.secondaryContainer)
        .labelAndAxisLinePadding(16.dp)
        .labelData { i ->
            val yMin = pointsData.minOf { it.y }
            val yMax = pointsData.maxOf { it.y }
            val yScale = (yMax - yMin) / steps
            (((i * yScale) + yMin).formatToSinglePrecision() + " ms")
        }.build()

    val data = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    lineStyle = LineStyle(
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    ),
                    IntersectionPoint(),
                    SelectionHighlightPoint(),
                    ShadowUnderLine(),
                    SelectionHighlightPopUp(),
                )
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(),
        backgroundColor = MaterialTheme.colorScheme.secondaryContainer
    )
    LineChart(
        lineChartData = data,
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}

fun graphData(state: PingState, target: String, maxPoints: Int = state.viewablePings): List<Point> {
    val list: MutableList<Point> = mutableListOf(Point(0.0F, 0.0F))
    var allPingsOfTarget = state.pings.filter { it.target == target }
    var iterator = 0
    list.clear()
    if (allPingsOfTarget.isEmpty()) {
        return mutableListOf(Point(0.0F, 0.0F))
    } else {
        for (ping in allPingsOfTarget) {
            list.add(Point(ping.id.toFloat(), ping.latency))
            iterator++
            if (iterator > maxPoints && iterator != 0 && list.size > maxPoints) {
                list.removeFirst()
            }
        }
    }

    if (list.isEmpty()) {list.add(Point(0.0F, 0.0F))}
    return list
}