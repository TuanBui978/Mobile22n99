package com.example.myapplication.presentation.profile

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isInvisible
import androidx.core.view.marginBottom
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.ShopRecycleViewAdapter
import com.example.myapplication.databinding.EditProfileDialogBinding
import com.example.myapplication.databinding.FragmentProfilesBinding
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.Shop
import com.example.myapplication.model.User
import java.util.zip.Inflater
import kotlin.math.log

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val USER_PARAM = "UserUid"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var userUid: String? = null
    private var param2: String? = null

    private var phoneNumber = ""
    private var address = ""
    private var position = 0
    private var gender = ""

    private lateinit var fragmentProfilesBinding: FragmentProfilesBinding

    private val profileViewModel: ProfileViewModel by viewModels { ProfileViewModel.Factory }

    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userUid = it.getString(USER_PARAM)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentProfilesBinding = FragmentProfilesBinding.inflate(inflater, container, false)
        val navController = findNavController()
        profileViewModel.getCurrentUser()
        profileViewModel.currentUserStatus.observe(this.viewLifecycleOwner) {
            status->
            if (status is InternetResult.Success) {
                currentUser = status.data
            }
        }

        val observer = Observer<InternetResult<User>>{
            status->
            when (status) {
                is InternetResult.Loading ->{
                    fragmentProfilesBinding.profileProgressBar.visibility = View.VISIBLE
                    fragmentProfilesBinding.warningTextView.visibility = View.GONE
                    fragmentProfilesBinding.profileLayout.visibility = View.GONE
                    fragmentProfilesBinding.editProfileTextView.visibility = View.GONE

                }
                is InternetResult.Success ->{
                    fragmentProfilesBinding.profileProgressBar.visibility = View.GONE
                    fragmentProfilesBinding.warningTextView.visibility = View.GONE
                    fragmentProfilesBinding.profileLayout.visibility = View.VISIBLE
                    fragmentProfilesBinding.editProfileTextView.visibility = View.VISIBLE
                    fragmentProfilesBinding.gmailTextView.text = status.data!!.email
                    fragmentProfilesBinding.phoneNumberTextView.text = status.data.phoneNumber
                    fragmentProfilesBinding.addressTextView.text = status.data.address
                    fragmentProfilesBinding.genderTextView.text = status.data.gender
                    phoneNumber = status.data.phoneNumber!!
                    address = status.data.address!!
                    gender = status.data.gender!!

                }
                is InternetResult.Failed ->{
                    fragmentProfilesBinding.profileProgressBar.visibility = View.GONE
                    fragmentProfilesBinding.warningTextView.visibility = View.VISIBLE
                    fragmentProfilesBinding.profileLayout.visibility = View.GONE
                    fragmentProfilesBinding.editProfileTextView.visibility = View.VISIBLE
//                    Toast.makeText(requireContext(), status.exception.message, Toast.LENGTH_SHORT).show()
                    Log.d("Profile", "onCreateView: ${status.exception.message}")
                }
            }
        }

        profileViewModel.profileStatus.observe(this.viewLifecycleOwner, observer)

        fragmentProfilesBinding.editProfileTextView.setOnClickListener{
            showEditProfileDialog()
        }

        profileViewModel.shopStatus.observe(this.viewLifecycleOwner) {
            status->
            when(status) {
                is InternetResult.Loading-> {
                    fragmentProfilesBinding.shopLoadingLayout.visibility = View.VISIBLE
                    fragmentProfilesBinding.shopWarningTextView.visibility = View.GONE
                    fragmentProfilesBinding.shopListLayout.visibility = View.GONE
                }
                is InternetResult.Success-> {
                    fragmentProfilesBinding.shopLoadingLayout.visibility = View.GONE
                    fragmentProfilesBinding.shopWarningTextView.visibility = View.GONE
                    fragmentProfilesBinding.shopListLayout.visibility = View.VISIBLE
                    val shops = status.data!!
                    val recyclerViewAdapter = ShopRecycleViewAdapter(shops)
                    val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    fragmentProfilesBinding.shopList.adapter = recyclerViewAdapter
                    fragmentProfilesBinding.shopList.layoutManager = layoutManager
                    val param = fragmentProfilesBinding.shopList.layoutParams as ViewGroup.MarginLayoutParams
                    if (shops.isEmpty()) {
                        param.bottomMargin = 0
                    }
                    else {
                        param.bottomMargin = 20
                    }
                    recyclerViewAdapter.onItemClickListener {
                        Toast.makeText(requireContext(), "Click", Toast.LENGTH_SHORT).show()
                    }
                }
                is InternetResult.Failed->{
                    fragmentProfilesBinding.shopLoadingLayout.visibility = View.GONE
                    fragmentProfilesBinding.shopWarningTextView.visibility = View.VISIBLE
                    fragmentProfilesBinding.shopListLayout.visibility = View.GONE
                }
            }
        }

        fragmentProfilesBinding.addShopButton.setOnClickListener {
            navController.navigate(R.id.add_shop)
        }

        return fragmentProfilesBinding.root
    }

    private fun showEditProfileDialog() {

        val builder = AlertDialog.Builder(this.requireContext())
        val dialogBinding = EditProfileDialogBinding.inflate(LayoutInflater.from(builder.context))
        builder.setView(dialogBinding.root)
        val spinnerArrayAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.gender_spinner_items, R.layout.simple_spinner_item)
        spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_drop_down_item)
        dialogBinding.genderSpinner.adapter = spinnerArrayAdapter
        for (i in 0..spinnerArrayAdapter.count) {
            if (spinnerArrayAdapter.getItem(i) == gender) {
                dialogBinding.genderSpinner.setSelection(i)
                break
            }
        }
        dialogBinding.addressEditText.setText(address)
        dialogBinding.phoneNumberEditText.setText(phoneNumber)
        builder.setCancelable(false)
        val dialog = builder.create()

        dialogBinding.okButton.setOnClickListener {
            phoneNumber = dialogBinding.phoneNumberEditText.text.toString()
            address = dialogBinding.addressEditText.text.toString()
            position = dialogBinding.genderSpinner.selectedItemPosition
            if (phoneNumber.isBlank() || address.isBlank()) {
                Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val gender = dialogBinding.genderSpinner.selectedItem.toString()
            val user = User(uid = userUid!!, phoneNumber = phoneNumber, address = address, gender = gender, email = currentUser!!.email)
            profileViewModel.createOrUpdateProfile(user)
        }
        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }


        val loadingBuilder = AlertDialog.Builder(this.requireContext())
        loadingBuilder.setView(R.layout.loading_dialog)
        loadingBuilder.setCancelable(false)
        val loading = loadingBuilder.create()

        profileViewModel.updateStatus.observe(this.viewLifecycleOwner) {
            status->
            when(status) {
                is InternetResult.Loading->{
                    dialog.dismiss()
                    loading.show()
                }
                is InternetResult.Success->{
                    loading.dismiss()
                    dialog.dismiss()
                    profileViewModel.getProfile(userUid!!)
                }
                is InternetResult.Failed->{
                    Toast.makeText(this.requireContext(), status.exception.message, Toast.LENGTH_SHORT).show()
                    loading.dismiss()
                    dialogBinding.addressEditText.setText(address)
                    dialogBinding.genderSpinner.setSelection(position)
                    dialog.show()
                }
            }
        }

        dialog.show()

    }

    override fun onResume() {
        super.onResume()
        profileViewModel.getProfile(userUid!!)
        profileViewModel.getShopListByUserId(userUid!!)
    }

    override fun onStart() {
        super.onStart()
        profileViewModel.getProfile(userUid!!)
        profileViewModel.getShopListByUserId(userUid!!)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfilesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(USER_PARAM, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}