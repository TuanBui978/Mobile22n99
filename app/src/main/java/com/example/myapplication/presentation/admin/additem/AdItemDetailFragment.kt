package com.example.myapplication.presentation.admin.additem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.adapter.UploadImageRecycleViewAdapter
import com.example.myapplication.adapter.VariantRecycleViewAdapter
import com.example.myapplication.databinding.FragmentAdItemDetailBinding
import com.example.myapplication.helper.getMultiImageBuilder
import com.example.myapplication.helper.getSingleImageBuilder
import com.example.myapplication.model.EnumGenderType
import com.example.myapplication.model.EnumType
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.Product
import com.example.myapplication.presentation.dialog.error.ErrorDialog
import com.example.myapplication.presentation.dialog.loading.LoadingDialog

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Use the [AdItemDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdItemDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var productId: String? = null
    private var param2: String? = null
    private lateinit var fragmentAdItemDetailBinding: FragmentAdItemDetailBinding
    private val adItemDetailViewModel: AdItemDetailViewModel by viewModels { AdItemDetailViewModel.Factory }
    private var product = Product()
    private lateinit var getSingleImage: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var getMultiImage: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            productId = it.getString(PRODUCT_ID)
            param2 = it.getString(ARG_PARAM2)
            adItemDetailViewModel.getProductById(productId!!)
        }
        getSingleImage = getSingleImageBuilder(this) {
            fragmentAdItemDetailBinding.addMainImageButton.foreground = ContextCompat.getDrawable(requireContext(), R.drawable.item_conner)
            fragmentAdItemDetailBinding.mainImage.setImageURI(it)
            product.mainImage = it.toString()
        }
        getMultiImage = getMultiImageBuilder(this) {
            val adapter = fragmentAdItemDetailBinding.imageUploadRecycleView.adapter as UploadImageRecycleViewAdapter
            adapter.addImages(it.map { uri-> uri.toString() })
            product.images = adapter.getImages()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        fragmentAdItemDetailBinding = FragmentAdItemDetailBinding.inflate(inflater, container, false)

        val loadingDialog = LoadingDialog(requireContext())
        val errorDialog = ErrorDialog(requireContext())
        adItemDetailViewModel.productStatus.observe(viewLifecycleOwner) {
            status->
            when(status) {
                is InternetResult.Loading->{
                    loadingDialog.show()
                }
                is InternetResult.Success->{
                    loadingDialog.dismiss()
                    onGetItemSuccess(status.data!!)
                }
                is InternetResult.Failed->{
                    loadingDialog.dismiss()
                    errorDialog.show()
                }
            }
        }

        // variant recycle view

        val variantAdapter = VariantRecycleViewAdapter()
        fragmentAdItemDetailBinding.variantRecycleView.adapter = variantAdapter
        fragmentAdItemDetailBinding.addVariant.setOnClickListener {
            val adapter = fragmentAdItemDetailBinding.variantRecycleView.adapter as VariantRecycleViewAdapter
            adapter.addVariant()
        }

        // gender spinner

        val genders = EnumGenderType.entries.map { it.displayString }
        val gendersAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, genders)
        gendersAdapter.setDropDownViewResource(R.layout.simple_spinner_drop_down_item)
        fragmentAdItemDetailBinding.genderSpinner.adapter = gendersAdapter
        fragmentAdItemDetailBinding.genderSpinner.post {
            fragmentAdItemDetailBinding.genderSpinner.dropDownVerticalOffset = fragmentAdItemDetailBinding.genderSpinner.height
        }
        fragmentAdItemDetailBinding.genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
        fragmentAdItemDetailBinding.typeSpinner.adapter = typesAdapter
        fragmentAdItemDetailBinding.typeSpinner.post {
            fragmentAdItemDetailBinding.typeSpinner.dropDownVerticalOffset = fragmentAdItemDetailBinding.typeSpinner.height
        }
        fragmentAdItemDetailBinding.typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
        fragmentAdItemDetailBinding.addMainImageButton.setOnClickListener {
            getSingleImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // add information image
        val imageAdapter = UploadImageRecycleViewAdapter()
        fragmentAdItemDetailBinding.imageUploadRecycleView.adapter = imageAdapter
        fragmentAdItemDetailBinding.addInformationImageButton.setOnClickListener {
            getMultiImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        val loadingBuilder = AlertDialog.Builder(requireContext())
        loadingBuilder.setView(R.layout.loading_dialog)
        loadingBuilder.setCancelable(false)
        val loading = loadingBuilder.create()

        adItemDetailViewModel.addStatus.observe(viewLifecycleOwner) {
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

        fragmentAdItemDetailBinding.addProductButton.setOnClickListener {
            onAddItemClick()
        }

        return fragmentAdItemDetailBinding.root
    }

    companion object {

        const val PRODUCT_ID = "param1"
        private const val ARG_PARAM2 = "param2"
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
            AdItemDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(PRODUCT_ID, productId)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun onAddItemClick() {
        val adapter = fragmentAdItemDetailBinding.variantRecycleView.adapter as VariantRecycleViewAdapter
        val variants = adapter.variant
        product.items = variants
        val name = fragmentAdItemDetailBinding.nameTextView.text.toString()
        val price = fragmentAdItemDetailBinding.priceTextView.text.toString().toFloatOrNull()
        val description = fragmentAdItemDetailBinding.descriptonTextView.text.toString()
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
        adItemDetailViewModel.addProduct(product)
    }
    private fun onGetItemSuccess(product: Product) {
        this.product = product
        val loadingDrawable = CircularProgressDrawable(requireContext())
        loadingDrawable.start()
        Glide.with(this).load(product.mainImage).centerCrop().error(R.drawable.error_image_photo_icon).placeholder(loadingDrawable).into(fragmentAdItemDetailBinding.mainImage)
        fragmentAdItemDetailBinding.addMainImageButton.foreground = ContextCompat.getDrawable(requireContext(), R.drawable.item_conner)
        val adapter = UploadImageRecycleViewAdapter(product.images)
        fragmentAdItemDetailBinding.imageUploadRecycleView.adapter = adapter
        fragmentAdItemDetailBinding.nameTextView.setText(product.name)
        for (i in 0 until EnumType.entries.size) {
            if (product.type == EnumType.entries[i]) {
                fragmentAdItemDetailBinding.typeSpinner.setSelection(i)
            }
        }
        for (i in 0 until EnumGenderType.entries.size) {
            if (product.gender == EnumGenderType.entries[i]) {
                fragmentAdItemDetailBinding.genderSpinner.setSelection(i)
            }
        }
        val variantAdapter = VariantRecycleViewAdapter(product.items)
        fragmentAdItemDetailBinding.variantRecycleView.adapter = variantAdapter
        fragmentAdItemDetailBinding.descriptonTextView.setText(product.description)
        fragmentAdItemDetailBinding.priceTextView.setText(product.price.toString())
        fragmentAdItemDetailBinding.addProductButton.text = getString(R.string.update_product)
        fragmentAdItemDetailBinding.addProductButton.setOnClickListener {
            onUpdateItemClick()
        }
    }
    private fun onUpdateItemClick() {
        val adapter = fragmentAdItemDetailBinding.variantRecycleView.adapter as VariantRecycleViewAdapter
        val variants = adapter.variant
        product.items = variants
        val name = fragmentAdItemDetailBinding.nameTextView.text.toString()
        val price = fragmentAdItemDetailBinding.priceTextView.text.toString().toFloatOrNull()
        val description = fragmentAdItemDetailBinding.descriptonTextView.text.toString()
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
        adItemDetailViewModel.updateProduct(product)
    }
}