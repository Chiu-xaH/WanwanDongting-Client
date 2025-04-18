package com.chiuxah.wanwandongting.logic.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.chiuxah.wanwandongting.MyApplication


object DataStoreManager {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "DataStore")
    private val dataStore = MyApplication.context.dataStore

//    private val ANIMATION_TYPE = intPreferencesKey("animation_types")

//    suspend fun saveAnimationType(type: Int) {
//        dataStore.edit { preferences ->
//            preferences[ANIMATION_TYPE] = type
//        }
//    }
//
//
//    val animationTypeFlow: Flow<Int> = dataStore.data
//        .map { preferences ->
//            preferences[ANIMATION_TYPE] ?: NavigateManager.AnimationTypes.FadeAnimation.code
//        }

    /* 用法
    val currentAnimationIndex by DataStoreManager.XXX.collectAsState(initial = 默认值)
     */
}




//
//fun main() {
//    val dataStore = DataStoreManager().dataStore
//
//    suspend fun incrementCounter() {
//        dataStore.edit { settings ->
//            val currentCounterValue = settings[EXAMPLE_COUNTER] ?: 0
//            settings[EXAMPLE_COUNTER] = currentCounterValue + 1
//        }
//    }
//    CoroutineScope(Job()).launch {
//        async { incrementCounter() }
//    }
//
//    val exampleCounterFlow: Flow<Int> = dataStore.data.map { preferences ->
//        // 无类型安全
//        preferences[EXAMPLE_COUNTER] ?: 0
//    }
//    println(exampleCounterFlow)
//}