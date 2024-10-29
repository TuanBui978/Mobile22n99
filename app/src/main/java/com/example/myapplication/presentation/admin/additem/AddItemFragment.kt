package com.example.myapplication.presentation.admin.additem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.myapplication.R
import com.example.myapplication.adapter.UploadImageRecycleViewAdapter
import com.example.myapplication.adapter.VariantRecycleViewAdapter
import com.example.myapplication.databinding.FragmentAddItemBinding
import com.example.myapplication.helper.checkAndRequestPermission
import com.example.myapplication.helper.getMultiImageBuilder
import com.example.myapplication.helper.getSingleImageBuilder
import com.example.myapplication.model.EnumGenderType
import com.example.myapplication.model.EnumType
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.Product
import com.example.myapplication.presentation.dialog.error.ErrorDialog

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val PRODUCT_ID = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddItemFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddItemFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var productId: String? = null
    private var param2: String? = null
    private lateinit var fragmentAddItemBinding: FragmentAddItemBinding
    private val addItemViewModel: AddItemViewModel by viewModels { AddItemViewModel.Factory }
    private val product = Product()
    private lateinit var getSingleImage: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var getMultiImage: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            productId = it.getString(productId)
            param2 = it.getString(ARG_PARAM2)
            addItemViewModel.getProductById(productId!!)
        }
        getSingleImage = getSingleImageBuilder(this) {
            fragmentAddItemBinding.addMainImageButton.foreground = ContextCompat.getDrawable(requireContext(), R.drawable.item_conner)
            fragmentAddItemBinding.mainImage.setImageURI(it)
            product.mainImage = it.toString()
        }
        getMultiImage = getMultiImageBuilder(this) {
            val adapter = fragmentAddItemBinding.imageUploadRecycleView.adapter as UploadImageRecycleViewAdapter
            adapter.addImages(it.map { uri-> uri.toString() })
            product.images = it.map { uri->uri.toString() }.toMutableList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        fragmentAddItemBinding = FragmentAddItemBinding.inflate(inflater, container, false)

        // variant recycle view

        val variantAdapter = VariantRecycleViewAdapter()
        fragmentAddItemBinding.variantRecycleView.adapter = variantAdapter
        fragmentAddItemBinding.addVariant.setOnClickListener {
            variantAdapter.addVariant()
        }

        // gender spinner

        val genders = EnumGenderType.entries.map { it.displayString }
        val gendersAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, genders)
        gendersAdapter.setDropDownViewResource(R.layout.simple_spinner_drop_down_item)
        fragmentAddItemBinding.genderSpinner.adapter = gendersAdapter
        fragmentAddItemBinding.genderSpinner.post {
            fragmentAddItemBinding.genderSpinner.dropDownVerticalOffset = fragmentAddItemBinding.genderSpinner.height
        }
        fragmentAddItemBinding.genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                product.gender = EnumGenderType.entries[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        // type spinner

        val types = EnumType.entries.map { it.displayString }
        val typesAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, types)
        typesAdapter.setDropDownViewResource(R.layout.simple_spinner_drop_down_item)
        fragmentAddItemBinding.typeSpinner.adapter = typesAdapter
        fragmentAddItemBinding.typeSpinner.post {
            fragmentAddItemBinding.typeSpinner.dropDownVerticalOffset = fragmentAddItemBinding.typeSpinner.height
        }
        fragmentAddItemBinding.typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                product.type = EnumType.entries[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        // main image button
        fragmentAddItemBinding.addMainImageButton.setOnClickListener {
            getSingleImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // add information image
        val imageAdapter = UploadImageRecycleViewAdapter()
        fragmentAddItemBinding.imageUploadRecycleView.adapter = imageAdapter
        fragmentAddItemBinding.addInformationImageButton.setOnClickListener {
            getMultiImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        val loadingBuilder = AlertDialog.Builder(requireContext())
        loadingBuilder.setView(R.layout.loading_dialog)
        val loading = loadingBuilder.create()

        val errorDialog = ErrorDialog(requireContext(), "Something was wrong when we try to create product")



        addItemViewModel.addStatus.observe(viewLifecycleOwner) {
            when (it) {
                is InternetResult.Loading->{
                    loading.show()
                }
                is InternetResult.Success->{
                    loading.dismiss()
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                is InternetResult.Failed->{
                    loading.dismiss()
                    errorDialog.show()
                }
            }
        }

        fragmentAddItemBinding.addProductButton.setOnClickListener {
            onAddItemClick()
        }

        return fragmentAddItemBinding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddItemFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddItemFragment().apply {
                arguments = Bundle().apply {
                    putString(PRODUCT_ID, productId)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun onAddItemClick() {
        val adapter = fragmentAddItemBinding.variantRecycleView.adapter as VariantRecycleViewAdapter
        val variants = adapter.variant
        product.items = variants
        val name = fragmentAddItemBinding.nameTextView.text.toString()
        val price = fragmentAddItemBinding.priceTextView.text.toString().toFloatOrNull()
        val description = fragmentAddItemBinding.descriptonTextView.text.toString()
        if (name.isBlank() || product.mainImage == null || product.images.isEmpty() || product.items.isEmpty() || description.isBlank() || price == null) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage(getString(R.string.you_need_to_fill_in_all_the_information_about_the_product))
            builder.setNegativeButton("OK") {
                dialog,_->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
            return
        }
        product.name = name
        product.price = price
        product.description = description
        addItemViewModel.addProduct(product)
    }
}