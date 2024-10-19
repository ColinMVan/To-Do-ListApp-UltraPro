package com.example.to_do_listapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private val tasks: MutableList<String>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var onTaskClickListener: ((Int) -> Unit)? = null

    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskText: TextView = view.findViewById(R.id.tv_task1)
//        val removeButton: Button = view.findViewById(R.id.markCompleteButton)

        init {
            view.setOnClickListener {
                // Creating the intent to open task_detail activity
                onTaskClickListener?.invoke(adapterPosition)
                val intent = Intent(view.context, task_detail::class.java)
                // the selected task and its index to task detail_detail
                intent.putExtra("task", tasks[adapterPosition])
                intent.putExtra("taskIndex", adapterPosition)
                //task_detail uses this to start
                (view.context as MainActivity).startActivity(intent)

            }
        }
    }

    fun setOnTaskClickListener(listener: (Int) -> Unit) {
        onTaskClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.child_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.taskText.text = "${position + 1}. ${tasks[position]}"

    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun removeTask(position: Int) {
        tasks.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, tasks.size)
    }

}

