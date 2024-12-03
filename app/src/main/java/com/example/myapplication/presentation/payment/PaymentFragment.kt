package com.example.myapplication.presentation.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentPaymentBinding
import com.example.myapplication.manager.Session
import com.example.myapplication.model.CartProduct
import com.example.myapplication.model.InternetResult
import com.example.myapplication.presentation.dialog.error.ErrorDialog
import com.example.myapplication.presentation.dialog.loading.LoadingDialog

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Use the [PaymentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PaymentFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var cartProducts: List<CartProduct>? = null
    private var param2: String? = null

    private val paymentViewModel: PaymentViewModel by viewModels { PaymentViewModel.Factory }

    private lateinit var fragmentPaymentBinding: FragmentPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            param2 = it.getString(ARG_PARAM2)
        }
        val args : PaymentFragmentArgs by navArgs()
        cartProducts = args.cartProducts.toMutableList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentPaymentBinding = FragmentPaymentBinding.inflate(layoutInflater, container, false)
        fragmentPaymentBinding.methodContainer.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != -1) {
                paymentViewModel.selectMethod(checkedId)
            }
            else {
                paymentViewModel.selectMethod(null)
            }
        }
        paymentViewModel.selectedMethod.observe(viewLifecycleOwner) {
            fragmentPaymentBinding.completeButton.isEnabled = (it != null)
        }

        fragmentPaymentBinding.completeButton.setOnClickListener {
            val user = Session.get.currentLogin!!
            val dialog = ErrorDialog(requireContext(), "Your account information is currently incomplete. Please update and try again.")
            if (user.address == null || user.email == null || user.gender == null || user.phoneNumber == null) {
                dialog.show()
                return@setOnClickListener
            }
            paymentViewModel.createOrder(cartProducts!!, user)
        }
        val loadingDialog = LoadingDialog(requireContext())
        paymentViewModel.createOrderStatus.observe(viewLifecycleOwner) {
            status->
            when (status) {
                is InternetResult.Loading->{
                    loadingDialog.show()
                }
                is InternetResult.Success->{
                    paymentViewModel.deleteCartProducts(cartProducts!!)
                    paymentViewModel.deleteCartProductStatus.observe(viewLifecycleOwner) {
                        it->
                        when (it) {
                            is InternetResult.Loading->{

                            }
                            is InternetResult.Failed->{
                                loadingDialog.dismiss()
                                val errorDialog = ErrorDialog(requireContext(), "Something wrong when we create your order. Please, try again later.")
                                errorDialog.show()
                            }
                            is InternetResult.Success->{
                                loadingDialog.dismiss()
                                findNavController().popBackStack()
                            }
                        }
                    }
                }
                is InternetResult.Failed->{
                    loadingDialog.dismiss()
                    val errorDialog = ErrorDialog(requireContext(), "Something wrong when we create your order. Please, try again later.")
                    errorDialog.show()
                }
            }
        }
        return fragmentPaymentBinding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PaymentFragment.
         */
        // TODO: Rename and change types and number of parameters
        private const val ARG_PARAM1 = "cartProductIds"
        private const val ARG_PARAM2 = "param2"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PaymentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}