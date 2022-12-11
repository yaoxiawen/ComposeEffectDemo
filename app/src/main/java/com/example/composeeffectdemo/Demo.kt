package com.example.composeeffectdemo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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