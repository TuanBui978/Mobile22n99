package com.example.myapplication.presentation.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.adapter.DetailColorAdapter
import com.example.myapplication.adapter.InfoImageRecycleViewAdapter
import com.example.myapplication.databinding.FragmentDetailBinding
import com.example.myapplication.manager.Session
import com.example.myapplication.model.CartProduct
import com.example.myapplication.model.EnumSize
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.Product
import com.example.myapplication.presentation.dialog.error.ErrorDialog
import com.example.myapplication.presentation.dialog.loading.LoadingDialog
import com.example.myapplication.presentation.payment.PaymentFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Use the [DetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var productId: String? = null
    private var param2: String? = null

    private val detailViewModel: DetailViewModel by viewModels { DetailViewModel.Factory }

    private lateinit var fragmentDetailBinding: FragmentDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            productId = it.getString(PRODUCT_ID)
            param2 = it.getString(ARG_PARAM2)
            detailViewModel.getProductById(productId!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentDetailBinding = FragmentDetailBinding.inflate(layoutInflater, container, false)
        if (savedInstanceState == null) {
            detailViewModel.productStatus.observe(viewLifecycleOwner) {
                status->
                when (status) {
                    is InternetResult.Loading->{
                        fragmentDetailBinding.mainLayout.visibility = View.GONE
                        fragmentDetailBinding.loadingLayout.visibility = View.VISIBLE
                        fragmentDetailBinding.Error.visibility = View.GONE
                    }
                    is InternetResult.Success->{
                        fragmentDetailBinding.mainLayout.visibility = View.VISIBLE
                        fragmentDetailBinding.loadingLayout.visibility = View.GONE
                        fragmentDetailBinding.Error.visibility = View.GONE
                        val product = status.data!!
                        //tên
                        fragmentDetailBinding.productNameTextView.text = status.data.name
                        //ảnh chính và adapter
                        val images:MutableList<String> = mutableListOf()
                        images.add(product.mainImage!!)
                        images.addAll(product.images)
                        val imageView = fragmentDetailBinding.imageView
                        val circularProgressDrawable = CircularProgressDrawable(imageView.context).apply {
                            strokeWidth = 5f
                            centerRadius = 30f
                            start()
                        }
                        Glide.with(this).load(product.mainImage).error(R.drawable.error_image_photo_icon).placeholder(circularProgressDrawable).into(imageView)
                        val adapter = InfoImageRecycleViewAdapter(images)
                        fragmentDetailBinding.imageRecycleView.adapter = adapter
                        adapter.setOnImageClickListener {
                                image ->
                            Glide.with(this).load(image).error(R.drawable.error_image_photo_icon).placeholder(circularProgressDrawable).into(imageView)
                        }

                        //recycle view chứa màu và xử lí  logic
                        val color = product.variants.map { it.color!! }.distinct().toMutableList()
                        val colorAdapter = DetailColorAdapter(color)
                        getSizeByColor(product, color[0])
                        fragmentDetailBinding.colorRecycleView.adapter = colorAdapter
                        colorAdapter.setOnColorClickListener {
                                selectColor->
                            getSizeByColor(product, selectColor)
                        }

                        //amount container
                        fragmentDetailBinding.priceTextView.text = getString(R.string.price_display, 0.00)
                        fragmentDetailBinding.countTextView.text = getText(R.string._0)
                        fragmentDetailBinding.removeButton.setOnClickListener {
                            var count = fragmentDetailBinding.countTextView.text.toString().toInt()
                            count--
                            count = if (count < 0) {
                                0
                            }
                            else {
                                count
                            }
                            val price =  product.price!! * count
                            fragmentDetailBinding.priceTextView.text = getString(R.string.price_display, price)
                            fragmentDetailBinding.countTextView.text = count.toString()
                        }
                        fragmentDetailBinding.addButton.setOnClickListener {
                            var count = fragmentDetailBinding.countTextView.text.toString().toInt()
                            count++
                            val price =  product.price!! * count
                            fragmentDetailBinding.priceTextView.text = getString(R.string.price_display, price)
                            fragmentDetailBinding.countTextView.text = count.toString()
                        }
                        fragmentDetailBinding.buyNowButton.setOnClickListener {
                            onBuyButtonClick(product)
                        }
                        fragmentDetailBinding.addToCartButton.setOnClickListener {
                            onAddToCartClick(product)
                        }
                    }
                    is InternetResult.Failed->{
                        fragmentDetailBinding.mainLayout.visibility = View.GONE
                        fragmentDetailBinding.loadingLayout.visibility = View.GONE
                        fragmentDetailBinding.Error.visibility = View.VISIBLE
                    }
                }
            }
        }

        return fragmentDetailBinding.root
    }

    private fun createCartProduct(product: Product): CartProduct? {
        val colorAdapter = fragmentDetailBinding.colorRecycleView.adapter as DetailColorAdapter
        val uid = Session.get.currentLogin!!.uid
        val size = when (fragmentDetailBinding.sizeContainer.checkedRadioButtonId) {
            fragmentDetailBinding.SSizeButton.id-> EnumSize.S
            fragmentDetailBinding.MSizeButton.id->EnumSize.M
            fragmentDetailBinding.LSizeButton.id->EnumSize.L
            fragmentDetailBinding.XLSizeButton.id ->EnumSize.XL
            fragmentDetailBinding.XXLSizeButton.id->EnumSize.XXL
            else->{
                ErrorDialog(requireContext(),
                    getString(R.string.please_choose_size_for_the_product)).show()
                return null
            }
        }
        val variants = product.variants.filter{ it.color == colorAdapter.getSelectedColor() && it.size == size }
        val variantId = if (variants.isEmpty()) {
            ErrorDialog(requireContext(),
                getString(R.string.something_was_wrong_when_we_get_variant_for_this_product_please_try_again_late)).show()
            return null
        }
        else {
            variants[0].id
        }
        val count = if (fragmentDetailBinding.countTextView.text.toString().toInt()!=0) {
            fragmentDetailBinding.countTextView.text.toString().toInt()
        }
        else {
            ErrorDialog(requireContext(), "Please add at least one product to proceed with the purchase.").show()
            return null
        }
        return CartProduct(product =  product, uid = uid, variantId =  variantId, count = count)
    }

    private fun onAddToCartClick(product: Product) {
        val loadingDialog = LoadingDialog(requireContext())
        detailViewModel.createCartProductStatus.removeObservers(viewLifecycleOwner)
        detailViewModel.createCartProductStatus.observe(viewLifecycleOwner) {
            status->
            when(status) {
                is  InternetResult.Loading->{
                    loadingDialog.show()
                }
                is InternetResult.Success->{
                    loadingDialog.dismiss()
                    Toast.makeText(requireContext(), "Add to cart success", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                is InternetResult.Failed->{
                    loadingDialog.dismiss()
                    ErrorDialog(requireContext()).show()
                }
            }
        }
        val cartProduct = createCartProduct(product)
        cartProduct?.let {
            detailViewModel.createCartProduct(cartProduct)
        }
    }

    private fun onBuyButtonClick(product: Product) {
        val loadingDialog = LoadingDialog(requireContext())
        detailViewModel.createCartProductStatus.removeObservers(viewLifecycleOwner)
        detailViewModel.createCartProductStatus.observe(viewLifecycleOwner) {
            status->
            when(status) {
                is  InternetResult.Loading->{
                    loadingDialog.show()
                }
                is InternetResult.Success->{
                    loadingDialog.dismiss()
                    val list = listOf(status.data!!)
                    val action = DetailFragmentDirections.actionDetailFragmentToPaymentFragment(list.toTypedArray())
                    findNavController().navigate(action)
                }
                is InternetResult.Failed->{
                    loadingDialog.dismiss()
                    ErrorDialog(requireContext()).show()
                }
            }
        }
        val cartProduct = createCartProduct(product)
        cartProduct?.let {
            detailViewModel.createCartProduct(cartProduct)
        }
    }

    private fun getSizeByColor(product: Product, selectColor: String) {
        val sizes = product.variants
            .filter { it.color == selectColor }
            .map { it.size }
        fragmentDetailBinding.sizeContainer.clearCheck()
        fragmentDetailBinding.SSizeButton.isEnabled = false
        fragmentDetailBinding.MSizeButton.isEnabled = false
        fragmentDetailBinding.LSizeButton.isEnabled = false
        fragmentDetailBinding.XLSizeButton.isEnabled = false
        fragmentDetailBinding.XXLSizeButton.isEnabled = false
        sizes.forEach { size ->
            when (size!!) {
                EnumSize.S -> fragmentDetailBinding.SSizeButton.isEnabled = true
                EnumSize.M -> fragmentDetailBinding.MSizeButton.isEnabled = true
                EnumSize.L -> fragmentDetailBinding.LSizeButton.isEnabled = true
                EnumSize.XL -> fragmentDetailBinding.XLSizeButton.isEnabled = true
                EnumSize.XXL -> fragmentDetailBinding.XXLSizeButton.isEnabled = true
            }
        }

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        const val PRODUCT_ID = "productId"
        const val ARG_PARAM2 = "param2"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putString(PRODUCT_ID, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}