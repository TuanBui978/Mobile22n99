package com.example.myapplication.presentation.admin.allitem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentAdAllItemBinding
import com.example.myapplication.model.InternetResult
import com.example.myapplication.presentation.mainfragment.MainFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AdAllItemFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdAllItemFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var fragmentAdAllItemBinding: FragmentAdAllItemBinding
    private val adAllItemViewModel: AdAllItemViewModel by viewModels { AdAllItemViewModel.Factory }

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
        fragmentAdAllItemBinding = FragmentAdAllItemBinding.inflate(layoutInflater, container, false)
        adAllItemViewModel.productStatus.observe(viewLifecycleOwner) {
            status->
            when (status) {
                is InternetResult.Loading->{
                    fragmentAdAllItemBinding.itemLoadingLayout.visibility = View.VISIBLE
                    fragmentAdAllItemBinding.emptyItem.visibility = View.GONE
                    fragmentAdAllItemBinding.itemError.visibility = View.GONE
                    fragmentAdAllItemBinding.itemLayout.visibility = View.GONE
                }
                is InternetResult.Failed->{
                    fragmentAdAllItemBinding.itemLoadingLayout.visibility = View.GONE
                    fragmentAdAllItemBinding.emptyItem.visibility = View.GONE
                    fragmentAdAllItemBinding.itemError.visibility = View.VISIBLE
                    fragmentAdAllItemBinding.itemLayout.visibility = View.GONE
                }
                is InternetResult.Success->{
                    fragmentAdAllItemBinding.itemLoadingLayout.visibility = View.GONE
                    fragmentAdAllItemBinding.itemError.visibility = View.GONE
                    if (status.data!!.isNotEmpty()) {
                        fragmentAdAllItemBinding.itemLayout.visibility = View.VISIBLE
                        fragmentAdAllItemBinding.emptyItem.visibility = View.GONE
                    }
                    else {
                        fragmentAdAllItemBinding.emptyItem.visibility = View.VISIBLE
                        fragmentAdAllItemBinding.itemLayout.visibility = View.GONE
                    }
                }
            }
        }
        fragmentAdAllItemBinding.addItemTextView.setOnClickListener {
            findNavController().navigate(R.id.action_adAllItemFragment_to_addItemFragment)
        }
        adAllItemViewModel.getAllItem()
        return fragmentAdAllItemBinding.root
    }





    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AllItemFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdAllItemFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}