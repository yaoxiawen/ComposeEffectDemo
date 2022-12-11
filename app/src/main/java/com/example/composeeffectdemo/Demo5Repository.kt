package com.example.composeeffectdemo

import android.media.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


class Demo5Repository {
    suspend fun load(): Image? {
        return withContext(Dispatchers.IO) {
            delay(1000)
            null
        }
    }
}

sealed class Result<T>() {
    object Loading : Result<Image>()
    object Error : Result<Image>()
    data class Success(val image: Image) : Result<Image>()
}