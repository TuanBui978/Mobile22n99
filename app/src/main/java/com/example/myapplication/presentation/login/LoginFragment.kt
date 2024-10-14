package com.example.myapplication.presentation.login

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentLoginBinding
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.User

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var fragmentLoginBinding: FragmentLoginBinding

    private val loginViewModel: LoginViewModel by viewModels { LoginViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val navController = findNavController()
        var passIsNull = true
        var emailIsNull = true
        // Inflate the layout for this fragment
        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false)

        fragmentLoginBinding.signInButton.isEnabled = false

        fragmentLoginBinding.registerTextView.setOnClickListener {
            navController.navigate(R.id.login_to_sign_up)
        }
        fragmentLoginBinding.emailEditText.addTextChangedListener {
            if (it.isNullOrBlank() ) {
                fragmentLoginBinding.signInButton.setBackgroundColor(Color.parseColor("#D9D9D9"))
                fragmentLoginBinding.signInButton.setTextColor(Color.parseColor("#7C7C7C"))
                fragmentLoginBinding.signInButton.isEnabled = false
                emailIsNull = true
            }
            else {
                emailIsNull = false
                if (!passIsNull) {
                    fragmentLoginBinding.signInButton.isEnabled = true
                    fragmentLoginBinding.signInButton.setBackgroundColor(Color.parseColor("#753532"))
                    fragmentLoginBinding.signInButton.setTextColor(Color.parseColor("#F4D6D5"))
                }
            }
        }
        fragmentLoginBinding.passwordEditText.addTextChangedListener {
            if (it.isNullOrBlank() ) {
                fragmentLoginBinding.signInButton.setBackgroundColor(Color.parseColor("#D9D9D9"))
                fragmentLoginBinding.signInButton.setTextColor(Color.parseColor("#7C7C7C"))
                fragmentLoginBinding.signInButton.isEnabled = false
                passIsNull = true
            }
            else {
                passIsNull = false
                if (!emailIsNull) {
                    fragmentLoginBinding.signInButton.isEnabled = true
                    fragmentLoginBinding.signInButton.setBackgroundColor(Color.parseColor("#753532"))
                    fragmentLoginBinding.signInButton.setTextColor(Color.parseColor("#F4D6D5"))
                }
            }
        }
        fragmentLoginBinding.signInButton.setOnClickListener{
            val email = fragmentLoginBinding.emailEditText.text.toString()
            val password = fragmentLoginBinding.passwordEditText.text.toString()
            loginViewModel.signIn(email, password)
        }
        val builder = AlertDialog.Builder(context)
        builder.setView(R.layout.loading_dialog)
        builder.setCancelable(false)
        val progress = builder.create()
        val statusObserver = Observer<InternetResult<User>> {
            status->
            when (status) {
                is InternetResult.Loading -> {
                    progress.show()
                }
                is InternetResult.Success -> {
                    navController.navigate(R.id.login_to_home)
                    progress.dismiss()
                }
                is InternetResult.Failed -> {
                    progress.dismiss()
                    Toast.makeText(context, status.exception.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        loginViewModel.status.observe(viewLifecycleOwner, statusObserver)

        return fragmentLoginBinding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}