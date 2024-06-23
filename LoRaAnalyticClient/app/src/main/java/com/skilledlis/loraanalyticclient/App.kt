package com.skilledlis.loraanalyticclient

import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.os.Build
import com.skilledlis.loraanalyticclient.UsbPermissionReceiver.Companion.ACTION_USB_PERMISSION

class App: Application() {

    val usbManager by lazy { getSystemService(Context.USB_SERVICE) as UsbManager }
    val usbRepository by lazy { UsbRepository.Base(usbManager) }

    private val bluetoothPermissionReceiver: BroadcastReceiver by lazy {
        UsbPermissionReceiver(usbRepository)
    }

    override fun onCreate() {
        super.onCreate()
        registerUsbAction()
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerUsbAction() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.registerReceiver(
                bluetoothPermissionReceiver,
                IntentFilter(ACTION_USB_PERMISSION),
                RECEIVER_EXPORTED
            )
        } else {
            this.registerReceiver(
                bluetoothPermissionReceiver,
                IntentFilter(ACTION_USB_PERMISSION)
            )
        }
    }

    fun unregisterUbsActionReceiver() {
        this.unregisterReceiver(bluetoothPermissionReceiver)
    }

}