package com.hodu.household_ledger.core.common

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow

data class AppError(
    val title: String = "오류",
    val message: String,
    val retry: (() -> Unit)? = null
)

object AppState {

    private val _errorEvent = MutableSharedFlow<AppError>(extraBufferCapacity = 10)
    val errorEvent = _errorEvent.asSharedFlow()

    private val _loadingCount = MutableStateFlow(0)

    /** 0보다 크면 로딩 중 */
    val loadingCount: StateFlow<Int> = _loadingCount

    // 중복 에러 방지: 같은 메시지가 2초 내 반복되면 무시
    private var lastErrorMessage: String? = null
    private var lastErrorTime: Long = 0

    fun showError(title: String = "오류", message: String, retry: (() -> Unit)? = null) {
        val now = System.currentTimeMillis()
        if (message == lastErrorMessage && now - lastErrorTime < 2000L) return
        lastErrorMessage = message
        lastErrorTime = now
        _errorEvent.tryEmit(AppError(title = title, message = message, retry = retry))
    }

    @Synchronized
    fun startLoading() {
        _loadingCount.value++
    }

    @Synchronized
    fun stopLoading() {
        _loadingCount.value = (_loadingCount.value - 1).coerceAtLeast(0)
    }

    suspend fun <T> withLoading(block: suspend () -> T): T {
        startLoading()
        return try {
            block()
        } finally {
            stopLoading()
        }
    }
}
