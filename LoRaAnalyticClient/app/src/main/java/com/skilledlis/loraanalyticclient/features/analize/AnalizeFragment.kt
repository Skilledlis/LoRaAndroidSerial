package com.skilledlis.loraanalyticclient.features.analize

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.skilledlis.loraanalyticclient.MainViewModel
import com.skilledlis.loraanalyticclient.databinding.FragmentMainBinding
import com.skilledlis.loraanalyticclient.features.base.BaseFragment

class AnalizeFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {

    companion object {
        fun newInstance() = AnalizeFragment()
    }

    private val viewModel: AnalizeViewModel by viewModels{AnalizeViewModel.Factory}



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.init()

        viewModel.rssi_snr.observe(viewLifecycleOwner){
            binding.RSSISNRTV.text = it
        }

        viewModel.messages.observe(viewLifecycleOwner){
//            binding.clearButton.text
            binding.messages.text = it.toString()

        }


        viewModel.statistics.observe(viewLifecycleOwner){
            binding.statisticTv.text = "${it.first} / ${it.second}%"
        }

        binding.clearButton.setOnClickListener{
            viewModel.clearMessages()
        }

        // TODO: Use the ViewModel
    }

}