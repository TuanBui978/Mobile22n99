package com.example.myapplication.presentation.admin.allorder

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.myapplication.R
import com.example.myapplication.adapter.OrderRecycleViewAdapter
import com.example.myapplication.adapter.ProductRecycleViewAdapter
import com.example.myapplication.databinding.FragmentAdAllOrderBinding
import com.example.myapplication.model.EnumStatus
import com.example.myapplication.model.InternetResult
import com.example.myapplication.presentation.admin.AdminFragment
import com.example.myapplication.presentation.dialog.datetime.DateTimePickerDialog
import com.example.myapplication.presentation.dialog.error.ErrorDialog
import com.example.myapplication.presentation.dialog.loading.LoadingDialog
import com.example.myapplication.presentation.mainfragment.MainFragment
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AdAllOrderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdAllOrderFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var fragmentAdAllOrderBinding: FragmentAdAllOrderBinding

    private val adAllOrderViewModel: AdAllOrderViewModel by viewModels { AdAllOrderViewModel.Factory }
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
        fragmentAdAllOrderBinding = FragmentAdAllOrderBinding.inflate(inflater, container, false)

        adAllOrderViewModel.orderStatus.observe(viewLifecycleOwner) {
            status->
            when (status) {
                is InternetResult.Loading->{
                    fragmentAdAllOrderBinding.orderLoadingLayout.visibility = View.VISIBLE
                    fragmentAdAllOrderBinding.emptyOrder.visibility = View.GONE
                    fragmentAdAllOrderBinding.orderError.visibility = View.GONE
                    fragmentAdAllOrderBinding.emptyOrder.visibility = View.GONE
                }
                is InternetResult.Failed->{
                    fragmentAdAllOrderBinding.orderLoadingLayout.visibility = View.GONE
                    fragmentAdAllOrderBinding.emptyOrder.visibility = View.GONE
                    fragmentAdAllOrderBinding.orderError.visibility = View.VISIBLE

                }
                is InternetResult.Success->{
                    fragmentAdAllOrderBinding.orderLoadingLayout.visibility = View.GONE
                    fragmentAdAllOrderBinding.orderError.visibility = View.GONE
                    if (status.data!!.isNotEmpty()) {
                        fragmentAdAllOrderBinding.emptyOrder.visibility = View.GONE
                        fragmentAdAllOrderBinding.itemRecycleView.visibility = View.VISIBLE
                        val adapter = OrderRecycleViewAdapter(status.data)
                        adapter.setOnStatusSelection {
                            adAllOrderViewModel.updateOrder(it)
                        }
                        fragmentAdAllOrderBinding.itemRecycleView.adapter = adapter
                    }
                    else {
                        fragmentAdAllOrderBinding.emptyOrder.visibility = View.VISIBLE
                    }
                }
            }
        }
        val loadingDialog = LoadingDialog(requireContext())
        val errorDialog = ErrorDialog(requireContext())
        adAllOrderViewModel.updateOrderStatus.observe(viewLifecycleOwner) {
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

        fromSetup()
        toSetUp()
        statusSpinnerSetUp()
        adAllOrderViewModel.getAllOrder()
        return fragmentAdAllOrderBinding.root
    }

    private fun fromSetup() {
        fragmentAdAllOrderBinding.fromDateTextView.setOnClickListener {
            val datetimeDialog = if (fragmentAdAllOrderBinding.fromDateTextView.text.isBlank()) {
                DateTimePickerDialog()
            }
            else {
                val format = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val dateString = fragmentAdAllOrderBinding.fromDateTextView.text
                val date = LocalDate.parse(dateString, format)
                DateTimePickerDialog(date.dayOfMonth, date.monthValue - 1, date.year)
            }
            datetimeDialog.setOnDateSetListener { day, month, year ->
                fragmentAdAllOrderBinding.fromDateTextView.text =
                    getString(R.string.date_display, day, month + 1, year)
                orderFilter()
            }
            datetimeDialog.show(childFragmentManager, "datePicker")
        }
    }

    private fun toSetUp() {
        fragmentAdAllOrderBinding.toDateTextView.setOnClickListener {
            val datetimeDialog = if (fragmentAdAllOrderBinding.toDateTextView.text.isBlank()) {
                DateTimePickerDialog()
            }
            else {
                val format = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val dateString = fragmentAdAllOrderBinding.toDateTextView.text
                val date = LocalDate.parse(dateString, format)
                DateTimePickerDialog(date.dayOfMonth, date.monthValue - 1, date.year)
            }
            datetimeDialog.setOnDateSetListener { day, month, year ->
                fragmentAdAllOrderBinding.toDateTextView.text =
                    getString(R.string.date_display, day, month + 1, year)
                orderFilter()
            }
            datetimeDialog.show(childFragmentManager, "datePicker")
        }
    }

    private fun orderFilter() {
        val from = if (fragmentAdAllOrderBinding.fromDateTextView.text.isBlank()) {
            null
        }
        else {
            val format = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val dateString = fragmentAdAllOrderBinding.fromDateTextView.text
            val localDate = LocalDate.parse(dateString, format)
            val date = Date.from(localDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant())
            Timestamp(date)
        }

        val to = if (fragmentAdAllOrderBinding.toDateTextView.text.isBlank()) {
            null
        }
        else {
            val format = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val dateString = fragmentAdAllOrderBinding.toDateTextView.text
            val localDate = LocalDate.parse(dateString, format)
            val date = Date.from(localDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant())
            Timestamp(date)
        }
        val status = if (fragmentAdAllOrderBinding.statusSpinner.selectedItemPosition == 0) {
            null
        }
        else {
            EnumStatus.entries[fragmentAdAllOrderBinding.statusSpinner.selectedItemPosition]
        }
        from?.let {
            Log.d("orderFilter: ", "$from")
        }
        to?.let {
            Log.d("orderFilter: ", "$to")
        }
        adAllOrderViewModel.filterOrder(from, to, status)
    }

    private fun statusSpinnerSetUp() {
        val status = listOf("None") + EnumStatus.entries.map { it.name }
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, status)
        arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_drop_down_item)
        var isInit = true
        fragmentAdAllOrderBinding.statusSpinner.adapter = arrayAdapter
        fragmentAdAllOrderBinding.statusSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (isInit) {
                    isInit = false
                }
                else {
                    orderFilter()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

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
         * @return A new instance of fragment AdAllOrderFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdAllOrderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}