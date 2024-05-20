package com.goalmaster.goal.view.viewgoal

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.goalmaster.R
import com.goalmaster.databinding.FragmentViewGoalBinding
import com.goalmaster.goal.data.entity.GoalState
import com.goalmaster.task.TaskAdapter
import com.goalmaster.todo.TodoAdapter
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ViewGoalFragment : Fragment(), GoalTasksCallbacks, AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentViewGoalBinding

    private val viewModel: ViewGoalViewModel by viewModels()

    private val args: ViewGoalFragmentArgs by navArgs()

    private lateinit var mAdapter: TaskAdapter
    private lateinit var todoAdapter: TodoAdapter

    private var selectedTab = "Task"

    private val todoTabName = "Todo"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewGoalBinding
            .inflate(inflater, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner

        mAdapter = TaskAdapter(this)
        binding.goalTasks.adapter = mAdapter
        binding.goalTasks.setHasFixedSize(true)
        observeGoalTasks()

        todoAdapter = TodoAdapter(viewModel)
        observeGoalTodos()

        binding.vm = viewModel
        viewModel.setUp(args.goalId)
        setupButton()
        setupTabs()

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.task_states_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.taskStateSpinner.adapter = adapter
            binding.taskStateSpinner.onItemSelectedListener = this
        }

        return binding.root
    }

    private fun setupTabs() {
        binding.taskTodoTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {
                if (selectedTab == tab.text) return
                selectedTab = tab.text!!.toString()
                if (selectedTab == todoTabName) {
                    binding.goalTasks.adapter = todoAdapter
                    binding.goalTasks.setHasFixedSize(true)
                } else {
                    binding.goalTasks.adapter = mAdapter
                    binding.goalTasks.setHasFixedSize(true)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().actionBar?.setBackgroundDrawable(ColorDrawable(Color.RED))
    }

    private fun observeGoalTasks() {
        lifecycleScope.launchWhenCreated {
            viewModel.tasks.collectLatest {
                mAdapter.submitList(it)
            }
        }
    }

    private fun observeGoalTodos() {
        viewModel.allTodos.observeForever {
                todoAdapter.submitList(it)
            }
    }

    private fun setupButton() {
        binding.viewGoalBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.createTaskButton.setOnClickListener {
            if (selectedTab == todoTabName) {
                viewModel.showAddTodo.value = true
                return@setOnClickListener
            }
            val action = ViewGoalFragmentDirections
                .actionViewGoalFragmentToCreateTaskFragment(args.goalId)
            findNavController().navigate(action)
        }

        binding.viewGoalEditButton.setOnClickListener {
            val action = ViewGoalFragmentDirections
                .actionViewGoalFragmentToEditGoalFragment(args.goalId)
            findNavController().navigate(action)
        }

        binding.addTodoInputLayout.setOnClickListener {
            if (it.id == binding.addTodoNameInputLayout.id
                || it.id == binding.addTodoNameInput.id ||
                it.id == binding.addTodoInputConfirm.id) return@setOnClickListener
            viewModel.showAddTodo.value = false
        }

        binding.addTodoInputConfirm.setOnClickListener {
            viewModel.createTodo()
        }
    }

    override fun openTask(taskId: Long) {
        val action = ViewGoalFragmentDirections
            .actionViewGoalFragmentToViewTaskFragment(taskId)
        findNavController().navigate(action)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.viewModelScope.launch {
            viewModel.selectedState.emit(id.toInt())
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }


}