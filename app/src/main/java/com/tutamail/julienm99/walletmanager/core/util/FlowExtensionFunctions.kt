package com.tutamail.julienm99.walletmanager.core.util

import androidx.lifecycle.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

context(ViewModel)
fun <T> Flow<T>.toStateFlow(default: T): StateFlow<T> {
    return stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), default)
}

context(ViewModel)
@OptIn(ExperimentalCoroutinesApi::class)
fun <T, R> StateFlow<T>.mapState(block: (T) -> R): StateFlow<R> {
    return mapLatest(block).toStateFlow(block(value))
}

context (ViewModel)
fun <T1, T2, R> combineState(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    transform: (T1, T2) -> R
): StateFlow<R> = combine(flow1, flow2) { o1, o2 ->
    transform(o1, o2)
}.toStateFlow(transform(flow1.value, flow2.value))

context (ViewModel)
fun <T1, T2, T3, R> combineState(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    flow3: StateFlow<T3>,
    transform: (T1, T2, T3) -> R
): StateFlow<R> = combine(flow1, flow2, flow3) { o1, o2, o3 ->
    transform(o1, o2, o3)
}.toStateFlow(transform(flow1.value, flow2.value, flow3.value))

context (ViewModel)
fun <T1, T2, T3, T4, R> combineState(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    flow3: StateFlow<T3>,
    flow4: StateFlow<T4>,
    transform: (T1, T2, T3, T4) -> R
): StateFlow<R> = combine(flow1, flow2, flow3, flow4) { o1, o2, o3, o4 ->
    transform(o1, o2, o3, o4)
}.toStateFlow(transform(flow1.value, flow2.value, flow3.value, flow4.value))

context (ViewModel)
fun <T1, T2, T3, T4, T5, R> combineState(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    flow3: StateFlow<T3>,
    flow4: StateFlow<T4>,
    flow5: StateFlow<T5>,
    transform: (T1, T2, T3, T4, T5) -> R
): StateFlow<R> = combine(flow1, flow2, flow3, flow4, flow5) { o1, o2, o3, o4, o5 ->
    transform(o1, o2, o3, o4, o5)
}.toStateFlow(transform(flow1.value, flow2.value, flow3.value, flow4.value, flow5.value))

fun <T1, T2, T3, T4, T5, T6, R> combine(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    transform: (T1, T2, T3, T4, T5, T6) -> R
): Flow<R> = combine(
    combine(flow1, flow2, flow3, ::Triple),
    combine(flow4, flow5, flow6, ::Triple)
) { t1, t2 ->
    transform(
        t1.first,
        t1.second,
        t1.third,
        t2.first,
        t2.second,
        t2.third
    )
}