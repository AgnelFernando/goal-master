package com.goalmaster.plan.view.planner

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.goalmaster.R
import com.goalmaster.databinding.FragmentPlannerBinding
import com.goalmaster.plan.data.entity.PlanState
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PlannerFragment : Fragment(), PlanTaskOptionImpl {

    private lateinit var binding: FragmentPlannerBinding

    private val viewModel: PlannerViewModel by viewModels()

    private lateinit var mAdapter: PlanTaskTempAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlannerBinding
            .inflate(inflater, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner

        mAdapter = PlanTaskTempAdapter(this)
        binding.planTasks.adapter = mAdapter
        binding.planTasks.isNestedScrollingEnabled = false
        viewModel.message.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
        observePlannedTasks()
        binding.vm = viewModel
        setupButtons()
        setUpBottomNavigation()
        return binding.root
    }

    private fun observePlannedTasks() {
        viewModel.plannedTask.observe(viewLifecycleOwner) {
            mAdapter.submitList(it)
        }
    }

    private fun setupButtons() {
        binding.createPlanButton.setOnClickListener {
            if (it.visibility != View.VISIBLE) return@setOnClickListener
            val action = PlannerFragmentDirections.actionPlannerFragmentToCreatePlanFragment()
            findNavController().navigate(action)
        }

        binding.planLockButton.setOnClickListener {
            if (viewModel.plan.value == null) return@setOnClickListener
            if (viewModel.plan.value!!.state == PlanState.CREATED) {
                openLockPlanConfirmation()
            }

            if (viewModel.plan.value!!.state == PlanState.LOCKED) {
                openCancelPlanConfirmation()
            }
        }

        binding.planCompleteButton.setOnClickListener {
            if (viewModel.plan.value == null && viewModel.plan.value!!.state != PlanState.LOCKED) return@setOnClickListener
            openCompletePlanConfirmation()
        }

        binding.addTaskButton.setOnClickListener {
            val action = PlannerFragmentDirections
                .actionPlannerFragmentToTaskPlannerFragment()
            findNavController().navigate(action)
        }

    }

    private fun openLockPlanConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.lock_plan_dialog_title))
            .setMessage(getString(R.string.lock_plan_dialog_message))
            .setIcon(android.R.drawable.ic_lock_lock)
            .setPositiveButton(
                R.string.lock_button_text
            ) { _, _ ->
                viewModel.updatePlanState(PlanState.LOCKED)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.plan_locked_toast), Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton(R.string.cancel, null).show()
    }

    private fun openCompletePlanConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.complete_plan_dialog_title))
            .setMessage(getString(R.string.complete_plan_dialog_message))
            .setIcon(R.drawable.baseline_check_24)
            .setPositiveButton(
                R.string.done_button_text
            ) { _, _ ->
                viewModel.updatePlanState(PlanState.COMPLETED)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.plan_done_toast), Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton(R.string.cancel, null).show()
    }

    private fun openCancelPlanConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.cancel_plan_dialog_title))
            .setMessage(getString(R.string.cancel_plan_dialog_message))
            .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
            .setPositiveButton(
                R.string.yes
            ) { _, _ ->
                viewModel.updatePlanState(PlanState.CANCELLED)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.plan_canceled_toast), Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton(R.string.no, null).show()
    }

    private fun setUpBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.navigationPlanner
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigationGoals -> {
                    val action = PlannerFragmentDirections
                        .actionPlannerFragmentToGoalListFragment()
                    findNavController().navigate(action)
                    true
                }
                else -> false
            }
        }
    }

    override fun onViewClicked(taskId: Long) {
        val action = PlannerFragmentDirections
            .actionPlannerFragmentToViewTaskFragment(taskId)
        findNavController().navigate(action)
    }

    override fun onDeleteClicked(taskId: Long) {
        viewModel.deletePlannedTask(taskId)
    }

}