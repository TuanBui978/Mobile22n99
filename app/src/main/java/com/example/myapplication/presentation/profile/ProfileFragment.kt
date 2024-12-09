package com.example.myapplication.presentation.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.EditProfileDialogBinding
import com.example.myapplication.databinding.FragmentProfilesBinding
import com.example.myapplication.helper.getSingleImageBuilder
import com.example.myapplication.helper.loadImageIntoImageView
import com.example.myapplication.manager.Session
import com.example.myapplication.model.EnumStatus
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.User
import com.example.myapplication.presentation.dialog.error.ErrorDialog
import com.example.myapplication.presentation.dialog.loading.LoadingDialog

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
    private lateinit var getSingleImage: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userUid = it.getString(USER_PARAM)
            param2 = it.getString(ARG_PARAM2)
        }
        getSingleImage = getSingleImageBuilder(this) {
            val user = Session.get.currentLogin!!
            profileViewModel.updateAvatar(user, it.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentProfilesBinding = FragmentProfilesBinding.inflate(inflater, container, false)
        val navController = findNavController()
        currentUser = Session.get.currentLogin

        fragmentProfilesBinding.userAvatar.setOnClickListener {
            getSingleImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        val loadingDialog = LoadingDialog(requireContext())
        val errorDialog = ErrorDialog(requireContext(), "Something was wrong when we try to update your avatar, please try again late.")
        profileViewModel.avatarStatus.observe(viewLifecycleOwner) {
            status->
            when(status) {
                is InternetResult.Loading->{
                    loadingDialog.show()
                }
                is InternetResult.Failed->{
                    loadingDialog.dismiss()
                    errorDialog.show()
                }
                is InternetResult.Success->{
                    loadingDialog.dismiss()
                    Session.get.currentLogin!!.avatar?.let {
                        loadImageIntoImageView(requireContext(),fragmentProfilesBinding.userAvatar, it.toUri(), R.drawable.avatar_default)
                    }

                }
            }
        }

        profileViewModel.orderStatus.observe(viewLifecycleOwner) {
            status->
            when (status) {
                is InternetResult.Success->{
                    val list = status.data
                    val confirming = list?.count {
                        it.status == EnumStatus.Confirming
                    }
                    val packing = list?.count {
                        it.status == EnumStatus.Packing
                    }
                    val delivering = list?.count {
                        it.status == EnumStatus.Delivering
                    }
                    fragmentProfilesBinding.confirmCount.text = confirming.toString()
                    fragmentProfilesBinding.packingCount.text = packing.toString()
                    fragmentProfilesBinding.deliveringCount.text = delivering.toString()
                }
                is InternetResult.Failed->{

                }
                is InternetResult.Loading->{

                }
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
                    if (Session.get.currentLogin!!.avatar != null) {
                        loadImageIntoImageView(requireContext(),fragmentProfilesBinding.userAvatar, Session.get.currentLogin!!.avatar!!.toUri(), R.drawable.avatar_default)
                    }
                    else {
                        fragmentProfilesBinding.userAvatar.setImageResource(R.drawable.avatar_default)
                    }
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
        return fragmentProfilesBinding.root
    }

    private fun showEditProfileDialog() {
        val builder = AlertDialog.Builder(this.requireContext())
        val dialogBinding = EditProfileDialogBinding.inflate(LayoutInflater.from(builder.context))
        builder.setView(dialogBinding.root)
        val spinnerArrayAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.gender_spinner_items, R.layout.simple_spinner_item)
        spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_drop_down_item)
        dialogBinding.genderSpinner.adapter = spinnerArrayAdapter
        for (i in 0 until spinnerArrayAdapter.count) {
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
            val user = User(
                uid = userUid!!,
                phoneNumber = phoneNumber,
                address = address,
                gender = gender,
                email = currentUser!!.email
            )
            Session.get.currentLogin!!.phoneNumber = phoneNumber
            Session.get.currentLogin!!.address = address
            Session.get.currentLogin!!.gender = gender
            Session.get.currentLogin!!.email = currentUser!!.email
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
                    Log.d("count", "loading update status")
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
//        profileViewModel.updateConfirmingItemCount.observe(viewLifecycleOwner) { status ->
//            Log.d("updateCF", "LiveData status: $status")
//            when (status) {
//                is InternetResult.Loading -> {
//                    Log.d("updateCF", "Loading state")
//                    fragmentProfilesBinding.profileProgressBar.visibility = View.VISIBLE
//                }
//                is InternetResult.Success -> {
//                    Log.d("updateCF", "Success state with count: ${status.data}")
//                    fragmentProfilesBinding.profileProgressBar.visibility = View.GONE
//                    fragmentProfilesBinding.confirmCount.text = status.data.toString()
//                }
//                is InternetResult.Failed -> {
//                    Log.e("updateCF", "Failed state: ${status.exception.message}")
//                    fragmentProfilesBinding.profileProgressBar.visibility = View.GONE
//                    fragmentProfilesBinding.confirmCount.text = "0"
//                }
//            }
//        }

        dialog.show()

    }

    override fun onStart() {
        super.onStart()
        profileViewModel.getProfile(userUid!!)
        profileViewModel.getListOrderById(userUid!!)
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