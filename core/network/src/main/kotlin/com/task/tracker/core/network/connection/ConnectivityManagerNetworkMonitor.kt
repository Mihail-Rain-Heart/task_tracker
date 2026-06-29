package com.task.tracker.core.network.connection

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.getSystemService
import com.task.tracker.core.common.di.ApplicationScope
import com.task.tracker.core.common.di.Fake
import com.task.tracker.core.common.network.Dispatcher
import com.task.tracker.core.common.network.Dispatchers
import com.task.tracker.core.common.network.NetworkMonitor
import com.task.tracker.core.network.fake.FakeWebServer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

internal class ConnectivityManagerNetworkMonitor @Inject constructor(
    @ApplicationScope scope: CoroutineScope,
    @param:ApplicationContext private val context: Context,
    @param:Dispatcher(dispatcher = Dispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    @param:Fake private val server: FakeWebServer,
) : NetworkMonitor {

    override val isServerAvailable: StateFlow<Boolean> get() = server.isStarted

    override val isOnline: StateFlow<Boolean> = callbackFlow {
        val connectivityManager = context.getSystemService<ConnectivityManager>()
        if (connectivityManager == null) {
            channel.trySend(false)
            channel.close()
            return@callbackFlow
        }

        val callback = object : ConnectivityManager.NetworkCallback() {

            private val networks = mutableSetOf<Network>()

            override fun onAvailable(network: Network) {
                networks += network
                channel.trySend(true)
            }

            override fun onLost(network: Network) {
                networks -= network
                channel.trySend(networks.isNotEmpty())
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, callback)

        channel.trySend(connectivityManager.isCurrentlyConnected())

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.flowOn(ioDispatcher)
        .conflate()
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = true
        )

    private fun ConnectivityManager.isCurrentlyConnected(): Boolean {
        val networkCapabilities = getNetworkCapabilities(activeNetwork) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
