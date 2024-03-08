package com.thalajaat.calyxcalculator.presentation.uis.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.thalajaat.calyxcalculator.R
import com.thalajaat.calyxcalculator.data.datasources.local.room.DropDownRateEntity
import com.thalajaat.calyxcalculator.databinding.RatesItemBinding

class RatesAdapter(
    private val context: Context,
    val onClick: (DropDownRateEntity) -> Unit,
) : RecyclerView.Adapter<RatesAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            RatesItemBinding.bind(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.rates_item,
                        parent,
                        false,
                    ),
            ),
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class MyViewHolder(private val binding: RatesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DropDownRateEntity) {
            binding.root.setOnClickListener {
                onClick(item)
            }
            if(item.isPinned) {
                binding.pin.visibility = View.VISIBLE
            }
            else{
                binding.pin.visibility = View.INVISIBLE
            }
            binding.name.text =  "${item.start}/${item.end}"

        }
    }

    private var items = listOf<DropDownRateEntity>()

    fun updateData(newItems: List<DropDownRateEntity>) {
        items = newItems.toMutableList()
        val diffUtil = DiffUtilCallback(items, newItems)
        val res = DiffUtil.calculateDiff(diffUtil)
        res.dispatchUpdatesTo(this)
    }

    fun getItems(): List<DropDownRateEntity> {
        return items
    }

    fun setItems(campaigns: List<DropDownRateEntity>?) {
        items = campaigns?.toMutableList() ?: emptyList()
        notifyDataSetChanged()
    }

    inner class DiffUtilCallback(
        private val oldList: List<DropDownRateEntity>,
        private val newList: List<DropDownRateEntity>,
    ) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition] ||
                    oldList[oldItemPosition] === newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }
    }
}