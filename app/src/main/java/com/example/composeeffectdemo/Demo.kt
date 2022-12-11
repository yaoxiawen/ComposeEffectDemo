package com.example.composeeffectdemo

import android.media.Image
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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

/**
 * DisposableEffect
 * key值改变或者组合函数离开组件树时会取消之前启动的协程，并会在取消协程前调用onDispose方法进行资源回收等相关操作
 */
@Composable
fun Demo4(backDispatcher: OnBackPressedDispatcher) {
    var addBackCallback by remember { mutableStateOf(false) }
    Row {
        Switch(checked = addBackCallback, onCheckedChange = {
            addBackCallback = !addBackCallback
        })
        Text(text = if (addBackCallback) "add back callback" else "not add back callback")
    }
    if (addBackCallback) {
        val backCallback = remember {
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.d("DisposableEffect", "onBack")
                }
            }
        }
        DisposableEffect(key1 = backDispatcher) {
            backDispatcher.addCallback(backCallback)
            //从组件树移除生效
            onDispose {
                Log.d("DisposableEffect", "onDispose")
                backCallback.remove()
            }
        }
    }
}

/**
 * produceState
 */
@Composable
fun Demo5() {
    var index by remember { mutableStateOf(0) }
    val repository = Demo5Repository()
    val result = Demo5Result(index, repository)
    Column {
        Button(onClick = {
            ++index
        }) {
            Text(text = "click:${index}")
        }
        Text(text = if (result.value is Result.Loading) "loading" else "ok")
    }
}

@Composable
private fun Demo5Result(index: Int, repository: Demo5Repository): State<Result<Image>> {
    return produceState(initialValue = Result.Loading as Result<Image>, index, repository) {
        //这里是主线程
        val result = repository.load()
        value = Result.Error
    }
}


