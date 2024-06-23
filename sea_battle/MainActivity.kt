package ru.liliani.tic_tac_toe

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.delay


private const val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"

class MainActivity : AppCompatActivity() {

    //    private val viewModel:MainViewModel by viewModel { }

    private val permissionIntent by lazy {
        val intent = Intent(UsbPermissionReceiver.ACTION_USB_PERMISSION)
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                PendingIntent.getBroadcast(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT or PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                PendingIntent.getBroadcast(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
            }

            else -> {
                PendingIntent.getBroadcast(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_MUTABLE
                )
            }
        }
    }

    lateinit var button: Button
    lateinit var textView: TextView

    private val viewModel: MainViewModel by viewModels { MainViewModel.Factory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.button)
        textView = findViewById(R.id.textView)

        viewModel.data.observe(this){
            textView.text = it
        }


        button.setOnClickListener {
            val list = (application as App).usbManager.deviceList.map { it.value }
            if (list.isNotEmpty()) {
                Log.d("AAAAAA", "list: $list")
                textView.text = list.toString()
                Thread.sleep(1000)
                (application as App).usbManager.requestPermission(list[0], permissionIntent)
            }

//            val acces = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY) as UsbAccessory?

//            acces?.let {
//            textView.text = list.toString()
//            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        (application as App).unregisterUbsActionReceiver()
    }
}


class MainViewModel(private val repo: UsbRepository) : ViewModel() {

    val data = repo.data.asLiveData()

//    private val _data: MutableLiveData<String> = MutableLiveData()
//    val data: LiveData<String> = _data

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])


                return MainViewModel(
                    (application as App).usbRepository
                ) as T
            }
        }
    }
}



