package ru.liliani.tic_tac_toe

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface UsbRepository {

    val data: Flow<String>


    suspend fun connect(device: UsbDevice)

    fun sendData(data: String)

    class Base(
        private val usbManager: UsbManager,
    ) : UsbRepository {

        override val data: MutableStateFlow<String> = MutableStateFlow("")

        private var device: UsbDevice? = null

        private val READ_ENDPOINT = 0
        private val WRITE_ENDPOINT = 1


        override suspend fun connect(device: UsbDevice) {
            Log.d("AAAAAA", "connect $device")
            this.device = device
            readData(device)
        }

        override fun sendData(data: String) {
            /*
                                val buffer:ByteArray = data.toByteArray()

                                device?.let {
                                    it.getInterface(WRITE_ENDPOINT).also { intf ->
                                        intf.getEndpoint(1)?.also { endpoint ->
                                            usbManager.openDevice(it)?.apply {

                                                claimInterface(intf, true)


                                                bulkTransfer(endpoint, buffer, buffer.size, 0)
                    //                            this.bulkTransfer(
                    //                                endpoint,
                    //                                buffer,
                    //                                buffer.size,
                    //                                0
                    //                            )

                    //                            return Printer.PrintUsb(
                    //                                context,
                    //                                this,
                    //                                endpoint,
                    //                            )
                    //                                .print(
                    //                                    operationTime,
                    //                                    vehicleName,
                    //                                    operation,
                    //                                )

                                            }
                                        }
                                    }
                                }*/
        }

        private suspend fun readData(device: UsbDevice) {
//            usbManager.openDevice(device).apply {
//                val inputStream: FileInputStream = FileInputStream(fileDescriptor)
//                inputStream.reader().use {
//                    val data = it.readText()
//                    this@Base.data.emit(data)
//                }
//            }

            device.let {
                it.getInterface(1).also { intf ->
                    intf.getEndpoint(1)?.also { endpoint ->
                        val byteA = ByteArray(256)
                        usbManager.openDevice(it)?.apply {
//
                            claimInterface(intf, true)

                            Log.d("AAAAAA", "run")
                            while (true) {
                                val readBytes = bulkTransfer(endpoint, byteA, 256, 0)
                                data.emit(
                                    String(

                                        byteA,
                                        0,
                                        readBytes,
                                        Charsets.US_ASCII
                                    ) + " -------- " + readBytes.toString()
                                )
                            }


//                            this.bulkTransfer(
//                                endpoint,
//                                buffer,
//                                buffer.size,
//                                0
//                            )

//                            return Printer.PrintUsb(
//                                context,
//                                this,
//                                endpoint,
//                            )
//                                .print(
//                                    operationTime,
//                                    vehicleName,
//                                    operation,
//                                )

                        }
                    }


                }
            }
        }
    }
}