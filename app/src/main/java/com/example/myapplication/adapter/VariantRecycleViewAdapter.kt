package com.example.myapplication.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.myapplication.R
import com.example.myapplication.databinding.VariantRecycleViewItemBinding
import com.example.myapplication.model.EnumSize
import com.example.myapplication.model.Item
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

class VariantRecycleViewAdapter(private var _variants: MutableList<Item> = mutableListOf()): Adapter<VariantRecycleViewAdapter.VariantViewHolder>() {
    val variant
        get() = _variants
    class VariantViewHolder(val viewBinding: VariantRecycleViewItemBinding) : ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariantViewHolder {
        val viewBinding = VariantRecycleViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VariantViewHolder((viewBinding))
    }

    override fun getItemCount(): Int {
        return _variants.size
    }

    override fun onBindViewHolder(holder: VariantViewHolder, position: Int) {
        val size = _variants[holder.adapterPosition].size?:EnumSize.S
        val color = _variants[holder.adapterPosition].color?:"#FFFFFFFF"
        val itemCount = _variants[holder.adapterPosition].count?:"0"
        holder.viewBinding.countEditText.setText(itemCount.toString())
        holder.viewBinding.addButton.setOnClickListener {
            val count = holder.viewBinding.countEditText.text.toString().toInt()+1
            holder.viewBinding.countEditText.setText(count.toString())
            _variants[holder.adapterPosition].count = count
        }
        holder.viewBinding.subButton.setOnClickListener {
            val count = if (holder.viewBinding.countEditText.text.toString().toInt()-1 < 0) {
                0
            }
            else {
                holder.viewBinding.countEditText.text.toString().toInt()-1
            }
            holder.viewBinding.countEditText.setText(count.toString())
            _variants[holder.adapterPosition].count = count
        }
        holder.viewBinding.removeButton.setOnClickListener {
            removeVariant(holder.adapterPosition)
        }
        val sizes = EnumSize.entries.map { it.display }
        val adapter = ArrayAdapter(holder.itemView.context, R.layout.type_spinner_item, sizes)
        adapter.setDropDownViewResource(R.layout.simple_spinner_drop_down_item)

        holder.viewBinding.sizeSpinner.adapter = adapter
        holder.viewBinding.sizeSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                _variants[holder.adapterPosition].size = EnumSize.entries[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
        holder.viewBinding.sizeSpinner.post {
            holder.viewBinding.sizeSpinner.dropDownVerticalOffset = holder.viewBinding.sizeSpinner.height
        }
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i) == size.display) {
                holder.viewBinding.sizeSpinner.setSelection(i)
                break
            }
        }
        holder.viewBinding.colorEditText.setText(color)

        val parseColor = try {
            Color.parseColor(color)
        }
        catch (_: Exception) {
            Color.WHITE
        }
        holder.viewBinding.colorPicker.setBackgroundColor(parseColor)

        val builder = ColorPickerDialog.Builder(holder.itemView.context)
        builder.setTitle("ColorPicker Dialog")
            .setPreferenceName("MyColorPickerDialog")
            .setPositiveButton("Select", object : ColorEnvelopeListener{
                override fun onColorSelected(envelope: ColorEnvelope, fromUser: Boolean) {
                    holder.viewBinding.colorPicker.setBackgroundColor(envelope.color)
                    val hex = "#${envelope.hexCode}"
                    holder.viewBinding.colorEditText.setText(hex)
                    _variants[holder.adapterPosition].color = hex
                }
            })
            .setNegativeButton("Cancel") {
                dialogInterface, i->
                dialogInterface.dismiss()
            }
            .attachAlphaSlideBar(true) // the default value is true.
            .attachBrightnessSlideBar(true)  // the default value is true.
            .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
        val colorPicker = builder.create()
        holder.viewBinding.colorPicker.setOnClickListener {
            colorPicker.show()
        }
    }

    fun addVariant() {
        _variants.add(Item())
        notifyItemInserted(_variants.size - 1)
    }

    private fun removeVariant(position: Int) { // Kiểm tra vị trí hợp lệ
            _variants.removeAt(position)
            notifyItemRemoved(position)
    }

}