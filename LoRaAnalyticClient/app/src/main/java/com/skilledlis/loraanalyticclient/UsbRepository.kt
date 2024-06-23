package com.skilledlis.loraanalyticclient

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface UsbRepository {

    val data: Flow<Data>
    val readState: StateFlow<Boolean>
    val deviceConnection: SharedFlow<Boolean>


    suspend fun connect(device: UsbDevice)

    //    suspend fun sendData(data: String)
    fun stopConnection()

    suspend fun readData()

    class Base(
        private val usbManager: UsbManager,
    ) : UsbRepository {

        override val data: MutableSharedFlow<Data> = MutableSharedFlow()
        override val readState: MutableStateFlow<Boolean> = MutableStateFlow(false)
        override val deviceConnection: MutableSharedFlow<Boolean> = MutableSharedFlow()

        private var device: UsbDevice? = null
        private var usbInterface: UsbInterface? = null
        private var connection: UsbDeviceConnection? = null

        private val SEND_ENDPOINT = 0
        private val READ_ENDPOINT = 1


        override suspend fun connect(device: UsbDevice) {
            this.device = device
            reading = true
            deviceConnection.emit(true)
            readState.emit(true)
            Log.d("READ_DATA", "connect end")
        }

        private var reading = false

//        override suspend fun sendData(data: String) {
//
//            readState.emit(false)
//            reading = false
//            connection?.releaseInterface(usbInterface)
//
//            delay(600)
//
//            Log.d("SEND_DATA", "sendData")
//
//            device?.let {
//                it.getInterface(1).also { intf ->
//                    usbInterface = intf
//                    intf.getEndpoint(SEND_ENDPOINT)?.also { endpoint ->
//                        usbManager.openDevice(it)?.apply {
//                            if (connection == null) {
//                                connection = this
//                                claimInterface(usbInterface, true)
//                            }
//                            val byteA = data.toByteArray(Charsets.US_ASCII)
//                            val a = bulkTransfer(endpoint, byteA, byteA.size, 500)
//                            Log.d("SEND_DATA", "aaa: $a")
//                            releaseInterface(usbInterface)
//                        }
//                    }
//                }
//                reading = true
//                readState.emit(true)
//            }
//
//        }


        override suspend fun readData() {
            device?.let {
                it.getInterface(1).also { intf ->
                    usbInterface = intf
                    intf.getEndpoint(READ_ENDPOINT)?.also { endpoint ->
                        usbManager.openDevice(it)?.apply {
                            if (connection == null)
                                connection = this
                            claimInterface(usbInterface, true)
                            val byteA = ByteArray(256)
                            while (reading) {
                                val length = bulkTransfer(endpoint, byteA, byteA.size, 500)
                                if (length > 0)
                                    parseData(byteA, length)
                            }
                            releaseInterface(usbInterface)
                        }
                    }
                }
            }
        }

        override fun stopConnection() {
            Log.d("READ_DATA", "stopConnection")
            connection?.releaseInterface(usbInterface)
            connection?.close()
        }

        private val textM: Byte = 6
        private val rssiM: Byte = 82

        private suspend fun parseData(byteA: ByteArray, length: Int) {

            if (byteA.first() == textM) {

                val all = String(byteA, 1, length)

                val g = all.split("\n")

                val m = g[0]
                val rssi = g[1]

                data.emit(Data.Valid(m))
                delay(50)
                data.emit(Data.RSSI_SNR(rssi))
            } else {

                val all = String(byteA, 1, length)

                val g = all.split("\n")

                val rssi = g[1]

                data.emit(Data.Invalidate())
                delay(50)
                data.emit(Data.RSSI_SNR("InVALID"+rssi))
                //TODO INVALID
            }

//            when (byteA.first()){
//                textM -> {
//                    data.emit(Data.Valid(String(byteA, 1, length)))
//                }
//
//                rssiM -> {
//                    data.emit(Data.RSSI_SNR(String(byteA, 0, length)))
//                }
//                else -> {
//                    data.emit(Data.Invalidate())
//                }
//            }

//            val message = String(byteA, 0, length)
//            Log.d("READ_DATA", message)
//            val d = when (message.first()) {
//                'p' -> {
//                    Data.Player(isReady = message[2] == 'r')
//                }
//                'a'-> Data.Attack(message[2].digitToInt(), message[4].digitToInt())
//                'd'-> Data.Damage(message[2].digitToInt(), message[4].digitToInt())
//                'm'-> Data.Miss(message[2].digitToInt(), message[4].digitToInt())
//                'w'-> Data.Win
//                else -> Data.Empty
//            }
//            data.emit(d)
        }
    }
}

