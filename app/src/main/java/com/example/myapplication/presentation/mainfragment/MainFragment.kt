package com.example.myapplication.presentation.mainfragment

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.DirectAction
import android.content.Context
import android.os.Bundle
import android.text.Layout
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentMainBinding
import com.example.myapplication.helper.checkAndRequestPermission
import com.example.myapplication.manager.Session
import com.example.myapplication.model.EnumGenderType
import com.example.myapplication.model.EnumType
import com.example.myapplication.model.User
import com.example.myapplication.presentation.home.HomeFragment
import com.example.myapplication.presentation.listitem.ListItemFragment
import com.example.myapplication.presentation.listitem.ListItemFragmentDirections
import com.example.myapplication.presentation.mainactivity.MainActivityViewModel
import com.google.android.material.navigation.NavigationView

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {
    // TODO: Rename and change types of parameters
    private var userUid: String? = null
    private var mParam2: String? = null
    private var fragmentMainBinding: FragmentMainBinding? = null
    private val mainFragmentViewModel: MainFragmentViewModel by viewModels { MainFragmentViewModel.Factory }
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private var navController: NavController? = null
    private var navHostFragment: NavHostFragment? = null
    private var reload = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestPermission(this)
        if (arguments != null) {
            userUid = requireArguments().getString(USER_PARAM)
            mParam2 = requireArguments().getString(ARG_PARAM2)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        fragmentMainBinding = FragmentMainBinding.inflate(inflater, container, false)
        requireActivity().onBackPressedDispatcher.addCallback {
            navController!!.popBackStack()
            if (navController!!.currentDestination?.id == R.id.homeFragment) {
                isEnabled = false
            }
        }
        val fragmentTransaction = childFragmentManager.beginTransaction()
        val bundle = bundleOf(USER_PARAM to userUid)
        if (savedInstanceState == null) {
            navHostFragment = NavHostFragment.create(R.navigation.nav_main_graph, bundle)
            fragmentTransaction.replace(R.id.nav_host_fragment_in_fragment, navHostFragment!!)
            fragmentTransaction.setPrimaryNavigationFragment(navHostFragment)
            fragmentTransaction.commit()
        }
        else {
            navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_in_fragment) as? NavHostFragment
        }
        val navigationView = fragmentMainBinding!!.navigationView
        navigationView.setNavigationItemSelectedListener(this)
        fragmentMainBinding!!.cartButton.setOnClickListener {
                navController!!.navigate(R.id.cartFragment, null,navOptions = navOptions {
                    popUpTo(R.id.homeFragment) {
                        saveState = true
                    }
                    launchSingleTop = true

                })
        }
        fragmentMainBinding!!.appLabel.setOnClickListener {
            navController!!.navigate(R.id.homeFragment, null,navOptions = navOptions {
                popUpTo(R.id.homeFragment) {
                    saveState = true
                }
                launchSingleTop = true
            })
        }
        fragmentMainBinding!!.menuButton.setOnClickListener {
            fragmentMainBinding!!.drawerLayout.openDrawer(GravityCompat.START)
        }
        fragmentMainBinding!!.searchBar.setOnFocusChangeListener { textView, isFocus ->
            val text = "Tsuki"
            val defaultWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 160f, resources.displayMetrics
            ).toInt()
            val expandedWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 225f, resources.displayMetrics
            ).toInt()

            // Xác định chiều rộng mục tiêu và cách cập nhật chuỗi
            val targetWidth = if (isFocus) expandedWidth else defaultWidth
            val startIndex = if (isFocus) text.length else 0
            val endIndex = if (isFocus) 0 else text.length

            if (isFocus) {
                val navOptions = navOptions {
                    popUpTo(R.id.homeFragment) {
                        saveState = true
                    }
                    launchSingleTop = true
                }
                if (reload) {
                    navController!!.navigate(R.id.listItemFragment, null, navOptions)
                    reload = false
                }
            }

            // Tạo ValueAnimator
            ValueAnimator.ofInt(textView.width, targetWidth).apply {
                duration = 300 // Thời gian chạy animation
                addUpdateListener { animation ->
                    // Cập nhật chiều rộng
                    val animatedValue = animation.animatedValue as Int
                    textView.layoutParams = textView.layoutParams.apply {
                        width = animatedValue
                    }

                    // Cập nhật text dần dần
                    val fraction = animation.animatedFraction
                    val currentIndex = (startIndex + fraction * (endIndex - startIndex)).toInt()
                        .coerceIn(0, text.length)
                    fragmentMainBinding!!.appLabel.text = text.subSequence(0, currentIndex)
                }
                start()
            }
        }
        fragmentMainBinding!!.searchBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                v.clearFocus()
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0) //
                true
            } else {
                false
            }
        }
        fragmentMainBinding!!.searchBar.doOnTextChanged { text, _, _, _ ->
            mainActivityViewModel.search(text.toString())
        }
        return fragmentMainBinding!!.root
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val navOptions = navOptions {
            popUpTo(R.id.homeFragment) {
                saveState = true
            }
            launchSingleTop = true
        }
        reload = true
        when (item.itemId) {
            R.id.log_out -> {
                mainFragmentViewModel.signOut()
                Session.get.logout()
                NavHostFragment.findNavController(this).navigate(R.id.sign_out, null, navOptions)
            }
            R.id.my_account -> {
                val bundle = bundleOf(USER_PARAM to userUid)
                navController!!.navigate(R.id.myAccountFragment, bundle, navOptions)
            }
            R.id.admin -> {
                navController!!.navigate(R.id.adminFragment, null, navOptions)
            }
            R.id.woman_tShirt -> {
                val bundle = bundleOf(
                    "type" to EnumType.TShirt.name,
                    "gender" to EnumGenderType.FEMALE.name
                )
                navController!!.navigate(R.id.listItemFragment, bundle, navOptions)
            }
            R.id.woman_hoodie -> {
                val bundle = bundleOf(
                    "type" to EnumType.Hoodie.name,
                    "gender" to EnumGenderType.FEMALE.name
                )
                navController!!.navigate(R.id.listItemFragment, bundle, navOptions)
            }
            R.id.woman_pants -> {
                val bundle = bundleOf(
                    "type" to EnumType.Pants.name,
                    "gender" to EnumGenderType.FEMALE.name
                )
                navController!!.navigate(R.id.listItemFragment, bundle, navOptions)
            }
            R.id.woman_shirt -> {
                val bundle = bundleOf(
                    "type" to EnumType.Shirt.name,
                    "gender" to EnumGenderType.FEMALE.name
                )
                navController!!.navigate(R.id.listItemFragment, bundle, navOptions)
            }
            R.id.woman_short -> {
                val bundle = bundleOf(
                    "type" to EnumType.Shorts.name,
                    "gender" to EnumGenderType.FEMALE.name
                )
                navController!!.navigate(R.id.listItemFragment, bundle, navOptions)
            }
            R.id.woman_sweater -> {
                val bundle = bundleOf(
                    "type" to EnumType.Sweater.name,
                    "gender" to EnumGenderType.FEMALE.name
                )
                navController!!.navigate(R.id.listItemFragment, bundle, navOptions)
            }
            R.id.woman_skirt -> {
                val bundle = bundleOf(
                    "type" to EnumType.Skirt.name,
                    "gender" to EnumGenderType.FEMALE.name
                )
                navController!!.navigate(R.id.listItemFragment, bundle, navOptions)
            }
            R.id.man_tShirt -> {
                val bundle = bundleOf(
                    "type" to EnumType.TShirt.name,
                    "gender" to EnumGenderType.MALE.name
                )
                navController!!.navigate(R.id.listItemFragment, bundle, navOptions)
            }
            R.id.man_shirt -> {
                val bundle = bundleOf(
                    "type" to EnumType.Shirt.name,
                    "gender" to EnumGenderType.MALE.name
                )
                navController!!.navigate(R.id.listItemFragment, bundle, navOptions)
            }
            R.id.man_hoodie -> {
                val bundle = bundleOf(
                    "type" to EnumType.Hoodie.name,
                    "gender" to EnumGenderType.MALE.name
                )
                navController!!.navigate(R.id.listItemFragment, bundle, navOptions)
            }
            R.id.man_sweater -> {
                val bundle = bundleOf(
                    "type" to EnumType.Sweater.name,
                    "gender" to EnumGenderType.MALE.name
                )
                navController!!.navigate(R.id.listItemFragment, bundle, navOptions)
            }
            R.id.man_short -> {
                val bundle = bundleOf(
                    "type" to EnumType.Shorts.name,
                    "gender" to EnumGenderType.MALE.name
                )
                navController!!.navigate(R.id.listItemFragment, bundle, navOptions)
            }
            R.id.man_pants -> {
                val bundle = bundleOf(
                    "type" to EnumType.Pants.name,
                    "gender" to EnumGenderType.MALE.name
                )
                navController!!.navigate(R.id.listItemFragment, bundle, navOptions)
            }
            R.id.hat -> {
                val bundle = bundleOf(
                    "type" to EnumType.Hat.name,
                    "gender" to EnumGenderType.BOTH.name
                )
                navController!!.navigate(R.id.listItemFragment, bundle, navOptions)
            }
            R.id.socks -> {
                val bundle = bundleOf(
                    "type" to EnumType.Socks.name,
                    "gender" to EnumGenderType.BOTH.name
                )
                navController!!.navigate(R.id.listItemFragment, bundle, navOptions)
            }
            else -> {
                reload = false
                navController!!.navigate(R.id.listItemFragment, null, navOptions)
            }
        }
        fragmentMainBinding!!.drawerLayout.closeDrawer(GravityCompat.START)
        return false
    }

    override fun onStart() {
        super.onStart()
        navController = navHostFragment!!.navController
    }


    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        const val USER_PARAM = "UserUid"
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
            args.putString(USER_PARAM, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}