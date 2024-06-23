package com.skilledlis.loraanalyticclient

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {


    private val navHostFragment: NavHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.main_host) as NavHostFragment }

    private val navController: NavController by lazy { navHostFragment.navController }


    private val viewModel: MainViewModel by viewModels { MainViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.deviceConnection.observe(this) {
            if (it) navController.navigate(R.id.action_connectFragment_to_analizeFragment)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        (application as App).unregisterUbsActionReceiver()
    }
}

