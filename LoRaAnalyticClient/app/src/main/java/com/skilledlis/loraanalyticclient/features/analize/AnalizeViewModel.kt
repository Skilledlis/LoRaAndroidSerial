package com.skilledlis.loraanalyticclient.features.analize

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.skilledlis.loraanalyticclient.App
import com.skilledlis.loraanalyticclient.Data
import com.skilledlis.loraanalyticclient.MainViewModel
import com.skilledlis.loraanalyticclient.UsbRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AnalizeViewModel(private val repo: UsbRepository) : ViewModel() {

    private var validMessageCount:Int = 0
    private var invalidMessageCount:Int = 0

    private fun updateStatistics() {
        val totalMessages = validMessageCount + invalidMessageCount
        if (totalMessages > 0) {
            val invalidPercentage = (invalidMessageCount.toDouble() / totalMessages) * 100
            statistics.postValue(totalMessages to invalidPercentage)
        }
    }


    val messages: MutableLiveData<List<String>> = MutableLiveData()
    val rssi_snr: MutableLiveData<String> = MutableLiveData()


    //Total / %invalidate
    val statistics: MutableLiveData<Pair<Int, Double>> = MutableLiveData()


    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.readData()
        }
        viewModelScope.launch(Dispatchers.IO) {
            repo.data.collect {
                when (it) {
                    is Data.Valid -> {


                        val newList: MutableList<String> = (messages.value ?: mutableListOf()).toMutableList()


//                             as MutableList<String>


                        newList.let { list ->
                            list.add(it.message)
                            messages.postValue(list)
                            validMessageCount++
                            updateStatistics()
                        }
                    }

                    is Data.Invalidate -> {
                        invalidMessageCount++
                        updateStatistics()
                    }

                    is Data.RSSI_SNR -> {
                        rssi_snr.postValue(it.value)
                    }
                }
            }
        }
    }

    fun clearMessages() {
        messages.postValue(listOf())
    }


    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])


                return AnalizeViewModel(
                    (application as App).usbRepository
                ) as T
            }
        }
    }
}