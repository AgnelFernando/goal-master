package com.goalmaster.task.view.viewtask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.goalmaster.R
import com.goalmaster.databinding.FragmentViewTaskBinding
import com.goalmaster.task.data.entity.TaskState
import com.goalmaster.task.data.entity.TaskTimeTracker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewTaskFragment : Fragment() {

    private lateinit var binding: FragmentViewTaskBinding

    private val viewModel: ViewTaskViewModel by viewModels()

    private val args: ViewTaskFragmentArgs by navArgs()

    private var currentTimeTracker: TaskTimeTracker? = null

    private var isTimeTrackerRunning: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewTaskBinding
            .inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.vm = viewModel
        viewModel.taskId.value = args.taskId
        setupButton()
        viewModel.currentTimeTracker.observe(viewLifecycleOwner) {
            this.currentTimeTracker = it
        }
        viewModel.isTimeTrackerRunning.observe(viewLifecycleOwner) {
            this.isTimeTrackerRunning = it
        }
        return binding.root
    }

    private fun setupButton() {
        binding.taskViewBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.taskViewEditButton.setOnClickListener {
            val action = ViewTaskFragmentDirections
                    .actionViewTaskFragmentToEditTaskFragment(args.taskId)
            findNavController().navigate(action)
        }

        binding.taskActionButton.setOnClickListener {
            viewModel.updateTaskState(TaskState.DONE)
        }

        binding.logTimeButton.setOnClickListener {
            if (isTimeTrackerRunning == false) {
                viewModel.saveTimeTracker()
            } else if (currentTimeTracker != null){
               viewModel.stopTimeTracker()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Time tracker running for other task", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}