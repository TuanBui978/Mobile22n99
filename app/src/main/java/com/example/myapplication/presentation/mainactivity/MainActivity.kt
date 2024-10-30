package com.example.myapplication.presentation.mainactivity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.helper.checkAndRequestPermission
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.User
import com.example.myapplication.presentation.home.HomeFragment
import com.example.myapplication.presentation.mainfragment.MainFragment


class MainActivity : AppCompatActivity() {


    private var activityMainBinding: ActivityMainBinding? = null

    private val viewModel: MainActivityViewModel by viewModels { MainActivityViewModel.Factory }

    private var navController: NavController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        val observer = Observer<InternetResult<User>>{
            status->
            when (status) {
                is InternetResult.Loading-> {

                }
                is InternetResult.Success-> {
                    val user = status.data
                    val bundle = bundleOf(MainFragment.USER_PARAM to user!!.uid)
                    try {
                        navController!!.navigate(R.id.loading_to_home, bundle)
                    }
                    catch (_: Exception) {

                    }

                }
                is InternetResult.Failed-> {
                    try {
                        navController!!.navigate(R.id.loading_to_sign_in)
                    }
                    catch (_: Exception) {

                    }
                }
            }
        }
        viewModel.status.observe(this, observer)
        viewModel.getCurrentUser()
        setContentView(activityMainBinding!!.root)
    }

    override fun onStart() {
        super.onStart()
    }

}