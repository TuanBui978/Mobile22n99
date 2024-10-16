package com.example.myapplication.presentation.mainfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentMainBinding
import com.google.android.material.navigation.NavigationView

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var fragmentMainBinding: FragmentMainBinding? = null
    private val mainFragmentViewModel: MainFragmentViewModel by viewModels { MainFragmentViewModel.Factory }
    private var navController: NavController? = null
    private var navHostFragment: NavHostFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1)
            mParam2 = requireArguments().getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        fragmentMainBinding = FragmentMainBinding.inflate(inflater, container, false)
        val fragmentTransaction = childFragmentManager.beginTransaction()
        navHostFragment = NavHostFragment.create(R.navigation.nav_main_graph)
        fragmentTransaction.replace(R.id.nav_host_fragment_in_fragment, navHostFragment!!)
        fragmentTransaction.setPrimaryNavigationFragment(navHostFragment)
        fragmentTransaction.commit()
        val navigationView = fragmentMainBinding!!.navigationView
        navigationView.setNavigationItemSelectedListener(this)
        fragmentMainBinding!!.cartButton.setOnClickListener {
            if (navController!!.currentDestination?.id != R.id.cartFragment) {
                navController!!.navigate(R.id.cartFragment)
            }
        }

        return fragmentMainBinding!!.root
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.log_out) {
            mainFragmentViewModel.signOut()
            NavHostFragment.findNavController(this).navigate(R.id.sign_out)
        }
        return false
    }

    override fun onStart() {
        super.onStart()
        navController = navHostFragment!!.navController
//        navHostFragment!!.host
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): MainFragment {
            val fragment = MainFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}