package com.example.karleuhapp.list

import androidx.fragment.app.viewModels

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil3.load
import com.example.karleuhapp.R
import com.example.karleuhapp.data.Api
import com.example.karleuhapp.databinding.FragmentTaskListBinding
import com.example.karleuhapp.detail.DetailActivity
import com.example.karleuhapp.user.UserActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import coil3.request.error


/**
 * A simple [Fragment] subclass.
 * Use the [TaskListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val adapter = TaskListAdapter(onDeleteClick = { task ->
        viewModel.remove(task)
    },
        onEditClick = { task ->
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("task", task)
            }
            editTask.launch(intent)
        }
    )

    private val viewModel: TaskListViewModel by viewModels()


    private val createTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val newTask = result.data?.getSerializableExtra("task") as? Task
            if (newTask != null) {
                viewModel.add(newTask)
            }
        }
    }

    private val editTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val newTask = result.data?.getSerializableExtra("task") as? Task
            if (newTask != null) {
                viewModel.edit(newTask)
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        val rootView = binding.root
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {

        val recyclerView = binding.recyclerviewID
        binding.recyclerviewID.adapter = adapter;

        //FAB add task Create task
        val fabAdd: FloatingActionButton = binding.NewTaskfloatingActionButton
        fabAdd.setOnClickListener {
            val intent = Intent(requireContext(), DetailActivity::class.java)
            createTask.launch(intent)
        }

        val imageViewAvatar: ImageView = binding.imageViewAvatar
        imageViewAvatar.load("https://goo.gl/gEgYUd")
        imageViewAvatar.setOnClickListener {
            val intent = Intent(requireContext(), UserActivity::class.java)
            startActivity(intent)
        }

        lifecycleScope.launch { // on lance une coroutine car `collect` est `suspend`
            viewModel.tasksStateFlow.collect { newList ->
                // cette lambda est exécutée à chaque fois que la liste est mise à jour dans le VM
                // -> ici, on met à jour la liste dans l'adapter
                adapter.submitList(newList)

            }
        }

        viewModel.refresh()
    }


    companion object {

        @JvmStatic
        fun newInstance() =
            TaskListFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            mySuspendMethod()
        }
    }

    private suspend fun mySuspendMethod() {

        if (view == null || !isAdded) {
            return
        }

        val imageViewAvatar = binding.imageViewAvatar

        try {
            val user = Api.userWebService.fetchUser().body()!!
            binding.userTextViewID.text = user.name
            imageViewAvatar.load(user.avatar) {
                error(R.drawable.ic_launcher_background) // image par défaut en cas d'erreur
            }
        } catch (e: Exception) {
            Log.e("TaskListFragment", "Erreur lors de la récupération des données utilisateur", e)
        }

        viewModel.refresh()
    }



}
