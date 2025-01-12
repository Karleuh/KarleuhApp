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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.example.karleuhapp.R
import com.example.karleuhapp.data.Api
import com.example.karleuhapp.databinding.FragmentTaskListBinding
import com.example.karleuhapp.detail.DetailActivity
import com.example.karleuhapp.user.UserActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import coil3.request.error

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [TaskListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val adapter = TaskListAdapter(onDeleteClick = { task ->
        //taskList.remove(task)
        viewModel.remove(task)
    },
        onEditClick = { task ->
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("task", task)
            }
            editTask.launch(intent)
        }
    )

    private lateinit var recyclerView: RecyclerView



    private val viewModel: TaskListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


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

        //Initialize binding
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
        // Définir un OnClickListener sur l'ImageView
        imageViewAvatar.setOnClickListener {
            // Créer un Intent pour démarrer SecondActivity
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TaskListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TaskListFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun refreshAdapter() {

        // le toList est important ici pcq submitList compare hmm (genre là ya besoin de faire une copie de la mutable list. est ce qu'il  y a pas 1000 listes ? qui pourrait dire)
        //adapter.submitList(taskList.toList())
        //Log.d("TaskListFragment", "Updated task list size: ${taskList.size}")

    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            mySuspendMethod()
        }
    }

    private suspend fun mySuspendMethod() {
        //
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
