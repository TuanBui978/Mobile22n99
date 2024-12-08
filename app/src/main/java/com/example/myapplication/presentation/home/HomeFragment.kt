package com.example.myapplication.presentation.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.databinding.FragmentListItemBinding


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var userUid: String? = null
    private var param2: String? = null
    private lateinit var fragmentHomeBinding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userUid = it.getString(USER_PARAM)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)

        fragmentHomeBinding.checkOutButton1.setOnClickListener {
            val navOptions = navOptions {
                popUpTo(R.id.homeFragment) {
                    saveState = true
                }
                launchSingleTop = true
            }
            findNavController().navigate(R.id.listItemFragment, null, navOptions)
        }

        fragmentHomeBinding.checkOutButton2.setOnClickListener {
            val navOptions = navOptions {
                popUpTo(R.id.homeFragment) {
                    saveState = true
                }
                launchSingleTop = true
            }
            findNavController().navigate(R.id.listItemFragment, null, navOptions)
        }
        return  fragmentHomeBinding.root
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val USER_PARAM = "UserUid"
        private const val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(userUid: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(USER_PARAM, userUid)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}