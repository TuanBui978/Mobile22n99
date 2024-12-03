package com.example.myapplication.presentation.signup

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentSignUpBinding
import com.example.myapplication.manager.Session
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.User
import com.example.myapplication.presentation.mainfragment.MainFragment
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.auth


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignUpFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var fragmentSignUpBinding: FragmentSignUpBinding? = null

    private val viewModel: SignUpViewModel by viewModels { SignUpViewModel.Factory }
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
        // Inflate the layout for this fragment
        fragmentSignUpBinding = FragmentSignUpBinding.inflate(inflater, container, false);

        val navController = findNavController()

        fragmentSignUpBinding!!.loginTextView.setOnClickListener {
            navController.navigate(R.id.sign_up_to_login)
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
                    val bundle = bundleOf(MainFragment.USER_PARAM to status.data!!.uid)
                    navController.navigate(R.id.sign_up_to_home, bundle)
                    Session.get.login(status.data)
                    progress.dismiss()
                }
                is InternetResult.Failed -> {
                    progress.dismiss()
                    Toast.makeText(context, status.exception.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.status.observe(viewLifecycleOwner, statusObserver)
        fragmentSignUpBinding!!.signUpButton.setOnClickListener{
            val email = fragmentSignUpBinding!!.emailEditTextView.text.toString()
            val password = fragmentSignUpBinding!!.passwordEditText.text.toString()
            val retypePassword = fragmentSignUpBinding!!.RetypePasswordEditText.text.toString()
            viewModel.signUp(email, password, retypePassword)
        }

        return fragmentSignUpBinding!!.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SignUpFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}