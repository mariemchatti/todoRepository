package com.example.toDoMariem.list

import TaskListAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.toDoMariem.R
import com.example.toDoMariem.Task
import com.example.toDoMariem.data.Api
import com.example.toDoMariem.data.TasksListViewModel
import com.example.toDoMariem.data.User
import com.example.toDoMariem.data.UserViewModel
import com.example.toDoMariem.detail.DetailActivity
import com.example.toDoMariem.user.UserActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskListFragment : Fragment(R.layout.fragment_task_list) {

    private var user: User? = null
    private val userModel: UserViewModel by viewModels()

    private val viewModel: TasksListViewModel by viewModels()
    private lateinit var userTextView: TextView
    private val adapterListener: TaskListListener = object : TaskListListener {

        override fun onClickDelete(task: Task) {
            viewModel.delete(task)
        }
        override fun onClickEdit(task: Task) {
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("TASK_TO_EDIT", task)
            editTask.launch(intent)
        }
    }

    private lateinit var createTask: ActivityResultLauncher<Intent>
    private lateinit var editTask: ActivityResultLauncher<Intent>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewTasks)
        val adapter = TaskListAdapter(adapterListener)
        recyclerView.adapter = adapter
        userTextView = view.findViewById(R.id.textViewHeader)


        // Récupérez le bouton d'ajout de tâche et gérez son clic
        val btnAddTask = view.findViewById<FloatingActionButton>(R.id.fabAddTask)
        btnAddTask.setOnClickListener {
            val intent = Intent(requireActivity(), DetailActivity::class.java)
            createTask.launch(intent)
        }

        createTask = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = result.data?.getParcelableExtra("task") as? Task
            task?.let {
                viewModel.add(task)
            }
        }
        lifecycleScope.launch {
            user = fetchUser()
            userTextView.text = user?.name
            viewModel.tasksStateFlow.collect { tasks ->
                adapter.submitList(tasks)
            }
        }

        editTask = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val editedTask = result.data?.getParcelableExtra<Task>("task")
                editedTask?.let { task ->
                    viewModel.update(task)
                }
            }
        }
        val avatarImageView = view.findViewById<ImageView>(R.id.imageViewAvatar)
        avatarImageView.setOnClickListener {
            val intent = Intent(requireContext(), UserActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        val avatarImageView = view?.findViewById<ImageView?>(R.id.imageViewAvatar)
        avatarImageView?.load(user?.avatar) {
            error(R.drawable.ic_baseline_person_24) // image par défaut en cas d'erreur
        }
        super.onResume()
        viewModel.refresh()
    }

    private suspend fun fetchUser(): User {
        return withContext(Dispatchers.IO) {
            Api.userWebService.fetchUser().body() !!
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }
}
