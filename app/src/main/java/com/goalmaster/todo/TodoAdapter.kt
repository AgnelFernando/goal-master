package com.goalmaster.todo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goalmaster.databinding.TodoItemBinding
import com.goalmaster.goal.view.viewgoal.ViewGoalViewModel
import com.goalmaster.todo.data.entity.Todo

class TodoAdapter(private val viewModel: ViewGoalViewModel):
    ListAdapter<Todo, RecyclerView.ViewHolder>(TODO_COMPARATOR) {

    init {
        setHasStableIds(true)
    }

    companion object {
        val TODO_COMPARATOR = object : DiffUtil.ItemCallback<Todo>() {
            override fun areContentsTheSame(
                oldItem: Todo,
                newItem: Todo
            ): Boolean = oldItem.id == newItem.id

            override fun areItemsTheSame(
                oldItem: Todo,
                newItem: Todo
            ): Boolean = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = TodoItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        (holder as TodoViewHolder).bind(data, viewModel)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id
    }

    class TodoViewHolder(item: View) : RecyclerView.ViewHolder(item) {

        private var binding: TodoItemBinding? = null

        constructor(binding: TodoItemBinding) : this(binding.root) {
            this.binding = binding
        }

        fun bind(data: Todo, vm: ViewGoalViewModel) {
            binding?.let {
                it.todo = data
                it.vm = vm
                itemView.invalidate()
            }
        }
    }
}