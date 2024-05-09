package com.example.plantsblooms

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.plantsblooms.databinding.ArPlantItemBinding

class ArPlantsRecyclerAdapter(private val list: List<PlantModel>) : RecyclerView.Adapter<ArPlantsRecyclerAdapter.ViewHolder>() {
    var selectedPosition = 0

    class ViewHolder(val itemBinding: ArPlantItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: PlantModel, selected: Boolean) {
            itemBinding.plantTitleTV.text = item.title
            itemBinding.typeTV.text = item.type
            itemBinding.pieceImage.setImageResource(item.image)
            if (selected) {
                itemBinding.root.strokeWidth = 10
            } else {
                itemBinding.root.strokeWidth = 0
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding =
            ArPlantItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], selectedPosition == position)
        onItemClick?.let { onItemClick ->
            holder.itemView.setOnClickListener {
                notifyItemChanged(selectedPosition)
                selectedPosition = position
                notifyItemChanged(position)
                onItemClick.onClick(list[position], position)
            }
        }
    }

    var onItemClick: OnItemClick? = null

    fun interface OnItemClick {
        fun onClick(item: PlantModel, position: Int)
    }
}