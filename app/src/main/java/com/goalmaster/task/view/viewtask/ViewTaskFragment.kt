package com.goalmaster.task.view.viewtask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.goalmaster.databinding.FragmentViewTaskBinding
import com.goalmaster.task.TaskState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewTaskFragment : Fragment() {

    private lateinit var binding: FragmentViewTaskBinding

    private val viewModel: ViewTaskViewModel by viewModels()

    private val args: ViewTaskFragmentArgs by navArgs()

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
    }

}