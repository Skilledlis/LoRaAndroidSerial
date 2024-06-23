package com.skilledlis.loraanalyticclient.features.connect

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.skilledlis.loraanalyticclient.App
import com.skilledlis.loraanalyticclient.UsbPermissionReceiver.Companion.ACTION_USB_PERMISSION
import com.skilledlis.loraanalyticclient.databinding.FragmentConnectBinding
import com.skilledlis.loraanalyticclient.features.base.BaseFragment

class ConnectFragment : BaseFragment<FragmentConnectBinding>(FragmentConnectBinding::inflate) {

    private val permissionIntent by lazy {

        val intent = Intent(ACTION_USB_PERMISSION)
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                intent.setPackage(requireContext().packageName)
                PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_MUTABLE)
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                PendingIntent.getBroadcast(
                    requireContext(),
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
            }
            else -> {
                PendingIntent.getBroadcast(
                    requireContext(), 0, intent, PendingIntent.FLAG_MUTABLE
                )
            }
        }
    }

    private val viewModel: ConnectViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener {
            val list = (requireActivity().application as App).usbManager.deviceList.map { it.value }
            if (list.isNotEmpty()) {
                (requireActivity().application as App).usbManager.requestPermission(
                    list[0],
                    permissionIntent
                )
            }
        }
    }


}