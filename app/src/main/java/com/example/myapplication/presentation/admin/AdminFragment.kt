package com.example.myapplication.presentation.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.myapplication.R
import com.example.myapplication.adapter.OrderRecycleViewAdapter
import com.example.myapplication.adapter.ProductRecycleViewAdapter
import com.example.myapplication.databinding.FragmentAdminBinding
import com.example.myapplication.decoreration.CustomDecoration
import com.example.myapplication.model.InternetResult
import com.example.myapplication.presentation.admin.additem.AdItemDetailFragment
import com.example.myapplication.presentation.admin.additem.AdItemDetailViewModel
import com.example.myapplication.presentation.dialog.error.ErrorDialog
import com.example.myapplication.presentation.dialog.loading.LoadingDialog

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AdminFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val adminViewModel by viewModels<AdminViewModel> { AdminViewModel.Factory }

    private lateinit var fragmentAdminBinding: FragmentAdminBinding

    private var position = -1;

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
        fragmentAdminBinding = FragmentAdminBinding.inflate(inflater, container, false)

        adminViewModel.orderStatus.observe(viewLifecycleOwner) {
            status->
            when (status) {
                is InternetResult.Loading->{
                    fragmentAdminBinding.orderLoadingLayout.visibility = View.VISIBLE
                    fragmentAdminBinding.orderRecycleView.visibility = View.GONE
                    fragmentAdminBinding.orderError.visibility = View.GONE
                    fragmentAdminBinding.emptyOrder.visibility = View.GONE
                }
                is InternetResult.Success->{
                    fragmentAdminBinding.orderLoadingLayout.visibility = View.GONE
                    fragmentAdminBinding.orderError.visibility = View.GONE
                    if (status.data!!.isNotEmpty()) {
                        fragmentAdminBinding.orderRecycleView.visibility = View.VISIBLE
                        fragmentAdminBinding.emptyOrder.visibility = View.GONE
                        val orders = status.data
                        val adapter = OrderRecycleViewAdapter(orders)
                        adapter.setOnStatusSelection { order ->
                            adminViewModel.updateOrder(order)
                        }
                        fragmentAdminBinding.orderRecycleView.adapter = adapter
                    }
                    else {
                        fragmentAdminBinding.orderRecycleView.visibility = View.GONE
                        fragmentAdminBinding.emptyOrder.visibility = View.VISIBLE
                    }
                }
                is InternetResult.Failed->{
                    fragmentAdminBinding.orderLoadingLayout.visibility = View.GONE
                    fragmentAdminBinding.orderRecycleView.visibility = View.GONE
                    fragmentAdminBinding.orderError.visibility = View.VISIBLE
                    fragmentAdminBinding.emptyOrder.visibility = View.GONE
                }
            }
        }

        adminViewModel.productStatus.observe(viewLifecycleOwner) {
            status->
            when (status) {
                is InternetResult.Loading->{
                    fragmentAdminBinding.itemLoadingLayout.visibility = View.VISIBLE
                    fragmentAdminBinding.itemError.visibility = View.GONE
                    fragmentAdminBinding.itemLayout.visibility = View.GONE
                }
                is InternetResult.Success->{
                    fragmentAdminBinding.itemLoadingLayout.visibility = View.GONE
                    fragmentAdminBinding.itemError.visibility = View.GONE
                    fragmentAdminBinding.itemLayout.visibility = View.VISIBLE
                    if (status.data!!.isNotEmpty()) {
                        val adapter = ProductRecycleViewAdapter(status.data.toMutableList(), requireContext(), R.string.remove)
                        adapter.onButtonClickListener {
                            product, pos ->
                            adminViewModel.deleteItemWithId(product.id!!)
                            position = pos
                        }
                        adapter.onItemClickListener {
                            val arg = bundleOf(AdItemDetailFragment.PRODUCT_ID to it.id)
                            findNavController().navigate(R.id.action_adminFragment_to_addItemFragment, arg)
                        }
                        fragmentAdminBinding.itemRecycleView.adapter = adapter
                        fragmentAdminBinding.itemRecycleView.visibility = View.VISIBLE
                    }
                    else {
                        fragmentAdminBinding.itemRecycleView.visibility = View.GONE
                    }

                }
                is InternetResult.Failed->{
                    fragmentAdminBinding.itemLoadingLayout.visibility = View.GONE
                    fragmentAdminBinding.itemError.visibility = View.VISIBLE
                    fragmentAdminBinding.itemLayout.visibility = View.GONE
                }
            }
        }

        val loadingDialog = LoadingDialog(requireContext())
        val errorDialog = ErrorDialog(requireContext())
        adminViewModel.deleteProductStatus.observe(viewLifecycleOwner) {
            status->
            when (status) {
                is InternetResult.Loading->{
                    loadingDialog.show()
                }
                is InternetResult.Success->{
                    loadingDialog.dismiss()
                    if (position != -1){
                        val adapter = fragmentAdminBinding.itemRecycleView.adapter as ProductRecycleViewAdapter
                         adapter.deleteItem(position)
                    }
                    position = -1
                }
                is InternetResult.Failed->{
                    loadingDialog.dismiss()
                    errorDialog.show()
                    position = -1
                }
            }
        }

        adminViewModel.updateOrderStatus.observe(viewLifecycleOwner) {
            status->
            when (status) {
                is InternetResult.Loading->{
                    loadingDialog.show()
                }
                is InternetResult.Success->{
                    loadingDialog.dismiss()
                }
                is InternetResult.Failed->{
                    loadingDialog.dismiss()
                    errorDialog.show()
                }
            }
        }

        val navController = findNavController()

        fragmentAdminBinding.shopItemTextView.setOnClickListener {
            navController.navigate(R.id.action_adminFragment_to_adAllItemFragment)
        }

        fragmentAdminBinding.shopOrderTextView.setOnClickListener {
            navController.navigate(R.id.action_adminFragment_to_adAllOrderFragment)
        }

        fragmentAdminBinding.addItemButton.setOnClickListener {
            navController.navigate(R.id.action_adminFragment_to_addItemFragment)
        }


        return fragmentAdminBinding.root
    }


    override fun onStart() {
        super.onStart()
        adminViewModel.getItemWithLimit(10)
        adminViewModel.getOrderWithLimit(10)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdminFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}