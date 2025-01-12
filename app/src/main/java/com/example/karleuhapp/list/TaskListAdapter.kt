package com.example.karleuhapp.list

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.karleuhapp.databinding.ItemTaskBinding
import androidx.recyclerview.widget.ListAdapter



object TasksDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task) : Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task) : Boolean {
        return oldItem == newItem
    }
}

class TaskListAdapter( private val onDeleteClick: (Task) -> Unit,
                       var onEditClick: (Task) -> Unit
    ) : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TasksDiffCallback) {

    // on utilise `inner` ici afin d'avoir accès aux propriétés de l'adapter directement
    inner class TaskViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            Log.d("TaskListAdapter", "Task title: ${task.title}")
            binding.itemTaskTitle.text = task.title
            binding.itemTaskDescription.text = task.description

            binding.removeTaskButton.setOnClickListener {
                onDeleteClick(task)
            }

            binding.editTaskButton.setOnClickListener {
                onEditClick(task)
            }
        }
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }


    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)  // Use getItem() for ListAdapter
        holder.bind(task)
    }
}