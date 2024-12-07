package com.example.myapplication.presentation.cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.myapplication.R
import com.example.myapplication.adapter.CartProductRecycleViewAdapter
import com.example.myapplication.databinding.FragmentCartBinding
import com.example.myapplication.manager.Session
import com.example.myapplication.model.InternetResult
import com.example.myapplication.presentation.dialog.error.ErrorDialog
import com.example.myapplication.presentation.dialog.loading.LoadingDialog
import com.example.myapplication.touchhelper.CustomItemTouchHelper
import com.example.myapplication.touchhelper.ItemTouchHelperAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CartFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var position : Int = -1

    private lateinit var fragmentCartBinding: FragmentCartBinding

    private val cartViewModel: CartViewModel by viewModels { CartViewModel.Factory }


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
        cartViewModel.getListCartProduct(Session.get.currentLogin!!.uid!!)
        val loadingDialog = LoadingDialog(requireContext())
        cartViewModel.deleteStatus.observe(viewLifecycleOwner){
            val adapter = fragmentCartBinding.itemRecycleView.adapter as CartProductRecycleViewAdapter
            when(it) {
                is InternetResult.Loading->{
                    loadingDialog.show()
                }
                is InternetResult.Success -> {
                    loadingDialog.dismiss()
                    if (position!=-1) {
                        adapter.deleteItem(position)
                    }
                }
                is InternetResult.Failed-> {
                    ErrorDialog(requireContext(), "Something wrong when we try to remove this product to your cart, please try again late.").show()
                    if (position!=-1) {
                        adapter.notifyItemChanged(position)
                    }
                }
            }
        }

        cartViewModel.cartProductStatus.observe(viewLifecycleOwner) {
            status->
            when(status) {
                is InternetResult.Loading->{
                    fragmentCartBinding.emptyItem.visibility = View.GONE
                    fragmentCartBinding.loadingLayout.visibility = View.VISIBLE
                    fragmentCartBinding.itemError.visibility = View.GONE
                    fragmentCartBinding.mainLayout.visibility = View.GONE
                }
                is InternetResult.Failed->{
                    fragmentCartBinding.emptyItem.visibility = View.GONE
                    fragmentCartBinding.loadingLayout.visibility = View.GONE
                    fragmentCartBinding.itemError.visibility = View.VISIBLE
                    fragmentCartBinding.mainLayout.visibility = View.GONE
                }
                is  InternetResult.Success->{
                    fragmentCartBinding.loadingLayout.visibility = View.GONE
                    fragmentCartBinding.itemError.visibility = View.GONE
                    if (status.data.isNullOrEmpty()) {
                        fragmentCartBinding.emptyItem.visibility = View.VISIBLE
                        fragmentCartBinding.mainLayout.visibility = View.GONE
                    }
                    else {
                        val list = status.data
                        fragmentCartBinding.emptyItem.visibility = View.GONE
                        fragmentCartBinding.mainLayout.visibility = View.VISIBLE
                        val adapter = CartProductRecycleViewAdapter(list.toMutableList())
                        val customItemTouchHelper = CustomItemTouchHelper(adapter)
                        val itemTouchHelper = ItemTouchHelper(customItemTouchHelper)
                        adapter.itemTouchHelper = itemTouchHelper
                        adapter.setOnDelete { cartProduct, i ->
                            position = i
                            cartViewModel.deleteCartProduct(cartProduct.id!!)
                        }
                        itemTouchHelper.attachToRecyclerView(fragmentCartBinding.itemRecycleView)
                        fragmentCartBinding.itemRecycleView.adapter = adapter

                        val totalPriceLiveData = adapter.getTotalPrice()
                        totalPriceLiveData.observe(viewLifecycleOwner) {
                            totalPrice->
                            val shipPrice = if (totalPrice == 0f) {
                                0f
                            }
                            else {
                                20f
                            }
                            if (shipPrice == 0f) {
                                fragmentCartBinding.checkOutButton.isEnabled = false
                            }
                            fragmentCartBinding.subtotalTextView.text = getString(R.string.price_display, totalPrice)
                            fragmentCartBinding.shippingTextView.text = getString(R.string.price_display, shipPrice)
                            fragmentCartBinding.totalPrice.text = getString(R.string.price_display, totalPrice + shipPrice)
                        }
                    }
                }
            }
        }
        // Inflate the layout for this fragment
        fragmentCartBinding = FragmentCartBinding.inflate(layoutInflater, container, false)
        fragmentCartBinding.checkOutButton.setOnClickListener {
            val adapter = fragmentCartBinding.itemRecycleView.adapter as CartProductRecycleViewAdapter
            val products = adapter.getProducts()
            val action = CartFragmentDirections.actionCartFragmentToPaymentFragment(products)
            findNavController().navigate(action)
        }
        return fragmentCartBinding.root
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CartFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CartFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}