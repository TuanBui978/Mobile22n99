package com.example.myapplication.presentation.mainactivity

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.manager.Session
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.User
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
                    val user = status.data!!
                    Session.get.login(user)
                    val bundle = bundleOf(MainFragment.USER_PARAM to user.uid)
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
        viewModel.getCurrentUser(applicationContext)
        setContentView(activityMainBinding!!.root)
    }



    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

}