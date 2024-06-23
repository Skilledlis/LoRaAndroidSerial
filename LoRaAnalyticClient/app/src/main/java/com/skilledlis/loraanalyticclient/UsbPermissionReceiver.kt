package com.skilledlis.loraanalyticclient

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Parcelable
import com.skilledlis.loraanalyticclient.UsbPermissionReceiver.Companion.ACTION_USB_PERMISSION
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UsbPermissionReceiver(
    private val deviceRepository: UsbRepository,
) : BroadcastReceiver() {

    companion object {
        const val ACTION_USB_PERMISSION =
            "com.skilledlis.loraanalyticclient.USB_PERMISSION"
    }


    override fun onReceive(context: Context, intent: Intent) {
        if (ACTION_USB_PERMISSION == intent.action) {
            val device = intent.parcelable<UsbDevice>(UsbManager.EXTRA_DEVICE)
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

//class UsbConnectionReceiver(
//    context: Context,
//    private val usbManager : UsbManager
//) : BroadcastReceiver() {
//
//    var usbStateChangeAction = "android.hardware.usb.action.USB_STATE"
//
//    @SuppressLint("MutableImplicitPendingIntent")
//    private val permissionIntent = PendingIntent.getBroadcast(
//        context,
//        0,
//        Intent(ACTION_USB_PERMISSION),
//        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
//    )
//
//    override fun onReceive(context: Context?, intent: Intent) {
//        val action = intent.action
//        if (action.equals(usbStateChangeAction, ignoreCase = true)) {
//            if (intent.extras!!.getBoolean("connected")) {
//                val list = usbManager.deviceList.map { it.value }
//                if (list.isNotEmpty()) {
//                    (context?.applicationContext as App).usbManager.requestPermission(
//                        list[0],
//                        permissionIntent
//                    )
//                }
//            }
//        }
//    }
//
//
//    private val permissionIntent1 by lazy {
//        val intent = Intent(ACTION_USB_PERMISSION)
//        when {
//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
//                intent.setPackage(context.packageName)
//                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE)
//            }
//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
//                PendingIntent.getBroadcast(
//                    context,
//                    0,
//                    intent,
//                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
//                )
//            }
//            else -> {
//                PendingIntent.getBroadcast(
//                    context, 0, intent, PendingIntent.FLAG_MUTABLE
//                )
//            }
//        }
//    }
//}

//override fun onReceive(context: Context, intent: Intent) {
//    if (ACTION_USB_PERMISSION == intent.action) {
//        val device = intent.parcelable<UsbDevice>(UsbManager.EXTRA_DEVICE)
//        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
//            if (device != null)
//                CoroutineScope(Dispatchers.IO).launch {deviceRepository.connect(device)
//                }
//        }
//    }
//}



private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

