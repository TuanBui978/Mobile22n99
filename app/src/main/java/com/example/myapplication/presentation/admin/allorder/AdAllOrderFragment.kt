package com.example.myapplication.presentation.admin.allorder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.myapplication.R
import com.example.myapplication.adapter.ProductRecycleViewAdapter
import com.example.myapplication.databinding.FragmentAdAllOrderBinding
import com.example.myapplication.model.InternetResult
import com.example.myapplication.presentation.admin.AdminFragment
import com.example.myapplication.presentation.mainfragment.MainFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AdAllOrderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdAllOrderFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var fragmentAdAllOrderBinding: FragmentAdAllOrderBinding

    private val adAllOrderViewModel: AdAllOrderViewModel by viewModels { AdAllOrderViewModel.Factory }
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
        fragmentAdAllOrderBinding = FragmentAdAllOrderBinding.inflate(inflater, container, false)

        adAllOrderViewModel.orderStatus.observe(viewLifecycleOwner) {
            status->
            when (status) {
                is InternetResult.Loading->{
                    fragmentAdAllOrderBinding.orderLoadingLayout.visibility = View.VISIBLE
                    fragmentAdAllOrderBinding.emptyOrder.visibility = View.GONE
                    fragmentAdAllOrderBinding.orderError.visibility = View.GONE
                    fragmentAdAllOrderBinding.emptyOrder.visibility = View.GONE
                }
                is InternetResult.Failed->{
                    fragmentAdAllOrderBinding.orderLoadingLayout.visibility = View.GONE
                    fragmentAdAllOrderBinding.emptyOrder.visibility = View.GONE
                    fragmentAdAllOrderBinding.orderError.visibility = View.VISIBLE
                    fragmentAdAllOrderBinding.orderLayout.visibility = View.GONE
                }
                is InternetResult.Success->{
                    fragmentAdAllOrderBinding.orderLoadingLayout.visibility = View.GONE
                    fragmentAdAllOrderBinding.orderError.visibility = View.GONE
                    if (status.data!!.isNotEmpty()) {
                        fragmentAdAllOrderBinding.orderLayout.visibility = View.VISIBLE
                        fragmentAdAllOrderBinding.emptyOrder.visibility = View.GONE
                    }
                    else {
                        fragmentAdAllOrderBinding.emptyOrder.visibility = View.VISIBLE
                        fragmentAdAllOrderBinding.orderLayout.visibility = View.GONE
                    }
                }
            }
        }
        adAllOrderViewModel.getAllOrder()
        return fragmentAdAllOrderBinding.root
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdAllOrderFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdAllOrderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}