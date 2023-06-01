package com.goalmaster.goal.view.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.goalmaster.databinding.FragmentEditGoalBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditGoalFragment : Fragment() {

    private val viewModel: EditGoalViewModel by viewModels()

    private lateinit var binding: FragmentEditGoalBinding

    private val args: EditGoalFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditGoalBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel
        viewModel.start(args.goalId)
        setupListeners()
        return binding.root
    }

    private fun setupListeners() {
        viewModel.saveGoalEvent.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        viewModel.deleteGoalEvent.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
            findNavController().navigateUp()
        }

        binding.editGoalToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.editGoalSaveButton.setOnClickListener {
            viewModel.saveGoal()
        }

        binding.editGoalDeleteButton.setOnClickListener {
            viewModel.deleteGoal()
        }
    }

}