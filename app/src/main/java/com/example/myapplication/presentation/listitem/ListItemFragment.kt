package com.example.myapplication.presentation.listitem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.myapplication.R
import com.example.myapplication.adapter.ProductRecycleViewAdapter
import com.example.myapplication.databinding.FragmentListItemBinding
import com.example.myapplication.manager.Session
import com.example.myapplication.model.CartProduct
import com.example.myapplication.model.EnumGenderType
import com.example.myapplication.model.EnumType
import com.example.myapplication.model.InternetResult
import com.example.myapplication.presentation.detail.DetailFragment
import com.example.myapplication.presentation.dialog.loading.LoadingDialog
import com.example.myapplication.presentation.mainactivity.MainActivityViewModel
import com.example.myapplication.presentation.mainfragment.MainFragment
import com.example.myapplication.presentation.mainfragment.MainFragmentViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListItemFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListItemFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val listItemViewModel: ListItemViewModel by viewModels { ListItemViewModel.Factory }
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var fragmentListItemBinding: FragmentListItemBinding

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

        fragmentListItemBinding = FragmentListItemBinding.inflate(layoutInflater, container, false)
        val args = ListItemFragmentArgs.fromBundle(requireArguments())
        if (args.type == null && args.gender == null) {
            listItemViewModel.getAllProduct()
        }
        else {
            val gender = EnumGenderType.entries.first {it.name == args.gender}
            val type = EnumType.entries.first { it.name == args.type }
            listItemViewModel.getProductBy(type, gender)
        }


        listItemViewModel.productStatus.observe(viewLifecycleOwner) {
            status->
            when (status) {
                is InternetResult.Loading->{
                    fragmentListItemBinding.itemLoadingLayout.visibility = View.VISIBLE
                    fragmentListItemBinding.itemError.visibility = View.GONE
                    fragmentListItemBinding.emptyItem.visibility = View.GONE
                    fragmentListItemBinding.itemLayout.visibility = View.GONE
                }
                is InternetResult.Failed->{
                    fragmentListItemBinding.itemLoadingLayout.visibility = View.GONE
                    fragmentListItemBinding.itemError.visibility = View.VISIBLE
                    fragmentListItemBinding.emptyItem.visibility = View.GONE
                    fragmentListItemBinding.itemLayout.visibility = View.GONE
                }
                is InternetResult.Success->{
                    fragmentListItemBinding.itemLoadingLayout.visibility = View.GONE
                    fragmentListItemBinding.itemError.visibility = View.GONE

                    if (status.data!!.isNotEmpty()) {
                        val adapter = ProductRecycleViewAdapter(status.data.toMutableList())
                        fragmentListItemBinding.emptyItem.visibility = View.GONE
                        adapter.onItemClickListener {
                            val arg = bundleOf(DetailFragment.PRODUCT_ID to it.id)
                            findNavController().navigate(R.id.action_listItemFragment_to_detailFragment, arg)
                        }
                        adapter.onButtonClickListener { product, pos ->
                            val arg = bundleOf(DetailFragment.PRODUCT_ID to product.id)
                            findNavController().navigate(R.id.action_listItemFragment_to_detailFragment, arg)
                        }
                        fragmentListItemBinding.itemRecycleView.adapter = adapter
                        fragmentListItemBinding.itemLayout.visibility = View.VISIBLE
                        mainActivityViewModel.filterQuery.observe(viewLifecycleOwner) {
                            adapter.filter(it)
                        }

                    }
                    else {
                        fragmentListItemBinding.emptyItem.visibility = View.VISIBLE
                        fragmentListItemBinding.itemLayout.visibility = View.GONE
                    }
                }
            }
        }

        return fragmentListItemBinding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListItemFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListItemFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}