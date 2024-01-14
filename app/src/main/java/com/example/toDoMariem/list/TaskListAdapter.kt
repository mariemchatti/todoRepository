import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.toDoMariem.R
import com.example.toDoMariem.Task
import com.example.toDoMariem.list.TaskListFragment
import com.example.toDoMariem.list.TaskListListener

object TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }
}

class TaskListAdapter(var listener: TaskListListener) : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TaskDiffCallback) {


    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskTitleTextView: TextView = itemView.findViewById(R.id.task_title)
        private val taskDescriptionTextView: TextView = itemView.findViewById(R.id.task_description)

        fun bind(task: Task) {
            taskTitleTextView.text = task.title
            taskDescriptionTextView.text = task.description ?: "No description"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = getItem(position)
        holder.bind(currentTask)
        // Gestionnaire de clic pour le bouton de suppression
        holder.itemView.findViewById<ImageButton>(R.id.delete_task_button).setOnClickListener {
            // Appel de la lambda onClickDelete avec la tâche actuelle
            listener.onClickDelete(currentTask)

        }

        holder.itemView.findViewById<ImageButton>(R.id.edit_task_button).setOnClickListener {
            // Appel de la lambda onClickDelete avec la tâche actuelle
            listener.onClickEdit(currentTask)
        }
    }
}
