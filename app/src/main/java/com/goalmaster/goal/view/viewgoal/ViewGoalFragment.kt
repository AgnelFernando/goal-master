package com.goalmaster.goal.view.viewgoal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.goalmaster.databinding.FragmentViewGoalBinding
import com.goalmaster.task.TaskAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ViewGoalFragment : Fragment(), GoalTasksCallbacks {

    private lateinit var binding: FragmentViewGoalBinding

    private val viewModel: ViewGoalViewModel by viewModels()

    private val args: ViewGoalFragmentArgs by navArgs()

    private lateinit var mAdapter: TaskAdapter

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

        binding.vm = viewModel
        viewModel.setUp(args.goalId)
        setupButton()
        return binding.root
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeGoalTasks() {
        lifecycleScope.launchWhenCreated {
            viewModel.tasks.collectLatest {
                mAdapter.submitList(it)
            }
        }
    }

    private fun setupButton() {
        binding.viewGoalBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.createTaskButton.setOnClickListener {
            val action = ViewGoalFragmentDirections
                .actionViewGoalFragmentToCreateTaskFragment(args.goalId)
            findNavController().navigate(action)
        }

        binding.viewGoalEditButton.setOnClickListener {
            val action = ViewGoalFragmentDirections
                .actionViewGoalFragmentToEditGoalFragment(args.goalId)
            findNavController().navigate(action)
        }
    }

    override fun openTask(taskId: Long) {
        val action = ViewGoalFragmentDirections
            .actionViewGoalFragmentToViewTaskFragment(taskId)
        findNavController().navigate(action)
    }


}