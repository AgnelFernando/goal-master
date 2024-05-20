package com.goalmaster.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.goalmaster.databinding.FragmentReviewPlannedTaskBinding
import com.goalmaster.plan.view.planner.PlanTaskOptionImpl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewTaskFragment : Fragment(), PlanTaskOptionImpl {

    private lateinit var binding: FragmentReviewPlannedTaskBinding

    private lateinit var mAdapter: TaskReviewAdapter

    private val viewModel: ReviewTaskViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewPlannedTaskBinding
            .inflate(inflater, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.reviewTasksToolbar.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.reviewTasksNextButton.setOnClickListener {
            val action = ReviewTaskFragmentDirections
                .actionReviewTaskFragmentToPlanTodayTaskFragment()
            findNavController().navigate(action)
        }
        setupAdapters()
        return binding.root
    }

    private fun setupAdapters() {
        mAdapter = TaskReviewAdapter(this)
        binding.plannedTasks.adapter = mAdapter
        viewModel.plannedTasks.observe(viewLifecycleOwner) {
            mAdapter.submitList(it)
        }
    }

    override fun onDoneClicked(taskId: Long) {
        viewModel.markAsDonePlannedTask(taskId)
    }

    override fun onMoveToNextDayClicked(taskId: Long) {
        viewModel.moveTaskPlanToNextDay(taskId)
    }

    override fun onViewClicked(taskId: Long) {}
    override fun onAddEventClicked(taskId: Long) {}
    override fun onDeleteClicked(taskId: Long) {}
}