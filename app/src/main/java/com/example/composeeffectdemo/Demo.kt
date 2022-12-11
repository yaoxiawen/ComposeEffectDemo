package com.example.composeeffectdemo

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * LaunchedEffect，启动协程
 */
@Composable
fun Demo1() {
    var state by remember { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(state) {
        scaffoldState.snackbarHostState.showSnackbar(
            message = "click message",
            actionLabel = "retry"
        )
    }
    Scaffold(scaffoldState = scaffoldState, content = {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Button(onClick = {
                state = !state
            }) {
                Text(text = "click")
            }
        }
    })
}

/**
 * rememberCoroutineScope，拿到协程作用域
 * 在可组合项外启动协程，手动控制一个或多个协程的生命周期
 * 可组合项传入一个回调，在回调里把rememberCoroutineScope传出去就是在可组合项外启动协程
 */
@Composable
fun Demo2() {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "抽屉内容区域")
            }
        },
        topBar = {
            TopAppBar(title = {}, navigationIcon = {
                IconButton(onClick = {
                    scope.launch { scaffoldState.drawerState.open() }
                }) {
                    Icon(imageVector = Icons.Filled.Menu, contentDescription = "")
                }
            })
        },
        content = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "内容区域")
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(text = { Text(text = "悬浮按钮") }, onClick = {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar("click 悬浮按钮")
                }
            })
        })
}

/**
 * key值更新，LaunchedEffect会重新启动
 * rememberUpdatedState，保证使用最新值且不会重启LaunchedEffect
 */
@Composable
fun Demo3() {
    val onTimeOut1: () -> Unit = { Log.d("rememberUpdatedState", "landing timeout 1.") }
    val onTimeOut2: () -> Unit = { Log.d("rememberUpdatedState", "landing timeout 2.") }
    var changeOnTimeOutState by remember { mutableStateOf(onTimeOut1) }
    Column() {
        Button(onClick = {
            changeOnTimeOutState = if (changeOnTimeOutState == onTimeOut1) {
                onTimeOut2
            } else {
                onTimeOut1
            }
        }) {
            Text(text = "click${if (changeOnTimeOutState == onTimeOut1) 1 else 2}")
        }
        LandingScreen(changeOnTimeOutState)
    }
}

@Composable
private fun LandingScreen(onTimeOut: () -> Unit) {
    val current by rememberUpdatedState(onTimeOut)
    LaunchedEffect(Unit) {
        Log.d("rememberUpdatedState", "LaunchedEffect")
        repeat(10) {
            delay(1000)
            Log.d("rememberUpdatedState", "delay ${it + 1} s")
        }
        current()
    }
}


