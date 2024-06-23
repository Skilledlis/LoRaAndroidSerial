package ru.liliani.tic_tac_toe

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbAccessory
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Parcelable
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UsbPermissionReceiver(
    private val deviceRepository: UsbRepository,
) : BroadcastReceiver() {

    companion object {
        const val ACTION_USB_PERMISSION =
            "ru.liliani.bvs.features.mainMenu.connectionToDevice.connectionUSBPrinter.USB_PERMISSION"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (ACTION_USB_PERMISSION == intent.action) {

            val device = intent.parcelable<UsbDevice>(UsbManager.EXTRA_DEVICE)
            Log.d("AAAAAA", "onReceive device: $device")
            if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                if (device != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        deviceRepository.connect(
                            device
                        )
                    }
                }
            }
        }
    }
}

private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

