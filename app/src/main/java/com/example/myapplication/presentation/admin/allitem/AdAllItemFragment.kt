package com.example.myapplication.presentation.admin.allitem

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.adapter.ProductRecycleViewAdapter
import com.example.myapplication.databinding.FragmentAdAllItemBinding
import com.example.myapplication.model.EnumGenderType
import com.example.myapplication.model.EnumType
import com.example.myapplication.model.InternetResult
import com.example.myapplication.presentation.admin.additem.AdItemDetailFragment
import com.example.myapplication.presentation.dialog.error.ErrorDialog
import com.example.myapplication.presentation.dialog.loading.LoadingDialog

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

    private var gender: Int = 0
    private var type: Int = 0


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

        val loadingDialog = LoadingDialog(requireContext())
        val errorDialog = ErrorDialog(requireContext())
        adAllItemViewModel.deleteProductStatus.observe(viewLifecycleOwner) {
            status->
            when (status) {
                is InternetResult.Loading->{
                    loadingDialog.show()
                }
                is InternetResult.Success->{
                    loadingDialog.dismiss()
                    if (position != -1) {
                        val adapter = fragmentAdAllItemBinding.itemRecycleView.adapter as ProductRecycleViewAdapter
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

        fragmentAdAllItemBinding = FragmentAdAllItemBinding.inflate(layoutInflater, container, false)
//        adAllItemViewModel.getAllItem()
        //gender spinner và logic
        val gender = listOf("None") + EnumGenderType.entries.map { it.displayString }
        val genderAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, gender)
        genderAdapter.setDropDownViewResource(R.layout.simple_spinner_drop_down_item)
        fragmentAdAllItemBinding.genderSpinner.adapter = genderAdapter
//        fragmentAdAllItemBinding.genderSpinner.setSelection(this.gender)
        fragmentAdAllItemBinding.genderSpinner.onItemSelectedListener = object: OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                this@AdAllItemFragment.gender = position
                val mGender = if (position == 0) {
                    null
                }
                else {
                    EnumGenderType.entries[position-1]
                }
                when (val pos = this@AdAllItemFragment.type) {
                    0 -> {
                        adAllItemViewModel.getProductByTypeAndGender(null, mGender)
                    }
                    else->{
                        val mType = EnumType.entries[pos-1]
                        adAllItemViewModel.getProductByTypeAndGender( mType, mGender)
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        //type spinner và logic
        val type = listOf("None") + EnumType.entries.map { it.displayString }
        val typeAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, type)
        typeAdapter.setDropDownViewResource(R.layout.simple_spinner_drop_down_item)
        fragmentAdAllItemBinding.typeSpinner.adapter = typeAdapter
//        fragmentAdAllItemBinding.typeSpinner.setSelection(this.type)
        fragmentAdAllItemBinding.typeSpinner.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                this@AdAllItemFragment.type = position
                val mGender = when (val pos =this@AdAllItemFragment.gender) {
                    0->{
                        null
                    }
                    else-> {
                        EnumGenderType.entries[pos]
                    }
                }
                when (position) {
                    0 -> {
                        adAllItemViewModel.getProductByTypeAndGender(null, mGender)
                    }
                    else->{
                        val mType = EnumType.entries[position-1]
                        adAllItemViewModel.getProductByTypeAndGender( mType, mGender)
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        adAllItemViewModel.productStatus.observe(viewLifecycleOwner) {
            status->
            when (status) {
                is InternetResult.Loading->{
                    fragmentAdAllItemBinding.itemLoadingLayout.visibility = View.VISIBLE
                    fragmentAdAllItemBinding.emptyItem.visibility = View.GONE
                    fragmentAdAllItemBinding.itemError.visibility = View.GONE

                }
                is InternetResult.Failed->{
                    fragmentAdAllItemBinding.itemLoadingLayout.visibility = View.GONE
                    fragmentAdAllItemBinding.emptyItem.visibility = View.GONE
                    fragmentAdAllItemBinding.itemError.visibility = View.VISIBLE

                }
                is InternetResult.Success->{
                    fragmentAdAllItemBinding.itemLoadingLayout.visibility = View.GONE
                    fragmentAdAllItemBinding.itemError.visibility = View.GONE
                    if (status.data!!.isNotEmpty()) {
                        fragmentAdAllItemBinding.itemRecycleView.visibility = View.VISIBLE
                        fragmentAdAllItemBinding.emptyItem.visibility = View.GONE
                        //recycle view và logic
                        val products = status.data.toMutableList()
                        val productAdapter = ProductRecycleViewAdapter(products, requireContext(), R.string.remove)
                        productAdapter.onButtonClickListener { product, pos ->
                            adAllItemViewModel.deleteItemWithId(product.id!!)
                            position = pos
                        }
                        productAdapter.onItemClickListener {
                            val arg = bundleOf(AdItemDetailFragment.PRODUCT_ID to it.id)
                            findNavController().navigate(R.id.action_adAllItemFragment_to_addItemFragment, arg)
                        }
                        fragmentAdAllItemBinding.itemRecycleView.adapter = productAdapter
                    }
                    else {
                        fragmentAdAllItemBinding.emptyItem.visibility = View.VISIBLE
                        fragmentAdAllItemBinding.itemRecycleView.visibility=View.GONE
                    }
                }
            }
        }
        fragmentAdAllItemBinding.addItemTextView.setOnClickListener {
            findNavController().navigate(R.id.action_adAllItemFragment_to_addItemFragment)
        }
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