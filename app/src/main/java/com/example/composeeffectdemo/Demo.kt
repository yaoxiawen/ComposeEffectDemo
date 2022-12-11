package com.example.composeeffectdemo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

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