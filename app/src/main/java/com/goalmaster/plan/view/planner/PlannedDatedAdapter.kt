package com.goalmaster.plan.view.planner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goalmaster.databinding.DateViewBinding
import java.time.LocalDate

class PlannedDatedAdapter(private val onDateSelected: (DateWarper, Int?) -> Unit) :
    ListAdapter<DateWarper, RecyclerView.ViewHolder>(DATES_COMPARATOR) {

    var selectedPos = RecyclerView.NO_POSITION

    private var first = true
    private val today = LocalDate.now()

    companion object {
        val DATES_COMPARATOR = object : DiffUtil.ItemCallback<DateWarper>() {
            override fun areContentsTheSame(
                oldItem: DateWarper,
                newItem: DateWarper
            ): Boolean {
                return oldItem.date == newItem.date
            }

            override fun areItemsTheSame(
                oldItem: DateWarper,
                newItem: DateWarper
            ): Boolean = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DateViewBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return PlannedDatedVH(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        if (holder is PlannedDatedVH) {
            if (first && today.equals(data.date)) {
                selectedPos = position
                onDateSelected(data, position)
                first = false
            }

            holder.binding?.isSelected = selectedPos == position

            holder.binding?.root?.setOnClickListener {
                val old = selectedPos
                selectedPos = position
                notifyItemChanged(old)
                notifyItemChanged(selectedPos)
                onDateSelected(data, null)
            }

            holder.bind(data)
        }
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).getId()
    }

    fun unselectDate() {
        val pos = selectedPos
        selectedPos = RecyclerView.NO_POSITION
        notifyItemChanged(pos)
    }

    class PlannedDatedVH(item: View) : RecyclerView.ViewHolder(item) {

        var binding: DateViewBinding? = null

        constructor(binding: DateViewBinding) : this(binding.root) {
            this.binding = binding
        }

        fun bind(data: DateWarper) {
            binding?.let {
                it.date = data.toString()
                it.hours = data.getTotalTime()
                itemView.invalidate()
            }
        }
    }
}