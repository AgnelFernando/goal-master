package com.goalmaster.plan.view.planner

import android.app.AlertDialog
import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.goalmaster.R
import com.goalmaster.databinding.FragmentPlannerBinding
import com.goalmaster.plan.data.entity.PlanState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.*


@AndroidEntryPoint
class PlannerFragment : Fragment(), PlanTaskOptionImpl {

    private lateinit var binding: FragmentPlannerBinding

    private val viewModel: PlannerViewModel by viewModels()

    private lateinit var mAdapter: PlanTaskTempAdapter

    private lateinit var dateAdapter: PlannedDatedAdapter

    private lateinit var createCalenderEvent: ActivityResultLauncher<Intent>

    private val pandrStartTime = LocalTime.of(22, 30)
    private val pandrEndTime = LocalTime.of(23, 59)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createCalenderEvent = registerForActivityResult(
            ActivityResultContracts
                .StartActivityForResult()
        ) {}

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlannerBinding
            .inflate(inflater, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
        setupAdapters()
        viewModel.message.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
        binding.vm = viewModel
        setupButtons()
        setUpBottomNavigation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        object : CountDownTimer(Long.MAX_VALUE, 2000) {

            override fun onTick(millisUntilFinished: Long) {
                val now = LocalTime.now()
                if (now.isAfter(pandrStartTime) && now.isBefore(pandrEndTime)) {
                    viewModel.showPlanAndReviewBanner.value = true
                }
            }

            override fun onFinish() {}
        }.start()
    }

    private fun setupAdapters() {
        mAdapter = PlanTaskTempAdapter(this)
        binding.planTasks.adapter = mAdapter
        binding.planTasks.isNestedScrollingEnabled = false
        viewModel.plannedTask.observe(viewLifecycleOwner) {
            mAdapter.submitList(it)
        }

        dateAdapter = PlannedDatedAdapter(this::onDateSelected)
        binding.dates.adapter = dateAdapter
        binding.dates.isNestedScrollingEnabled = false

        viewModel.allTasks.observe(viewLifecycleOwner) {
            dateAdapter.submitList(viewModel.getTaskGroupByDates())
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

        binding.allPlanedTasks.root.setOnClickListener {
            dateAdapter.unselectDate()
            viewModel.selectedDate.value = null
        }

        binding.reviewAndPlanBanner.setOnClickListener {
            val action = PlannerFragmentDirections
                .actionPlannerFragmentToReviewAndPlanFragment()
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
                R.id.navigationAnalytics -> {
                    val action = PlannerFragmentDirections
                        .actionPlannerFragmentToAnalyticsFragment()
                    findNavController().navigate(action)
                    true
                }
//                R.id.navigationSettings -> {
//                    val action = PlannerFragmentDirections
//                        .actionPlannerFragmentToSettingFragment()
//                    findNavController().navigate(action)
//                    true
//                }
                else -> false
            }
        }
    }

    override fun onDoneClicked(taskId: Long) {
        viewModel.markAsDonePlannedTask(taskId)
    }

    override fun onViewClicked(taskId: Long) {
        val action = PlannerFragmentDirections
            .actionPlannerFragmentToViewTaskFragment(taskId)
        findNavController().navigate(action)
    }

    override fun onAddEventClicked(taskId: Long) {
        val data = viewModel.getPlannedTaskById(taskId) ?: return
        val planTask = data.planTask ?: return
        val task = data.task

        val uri: Uri =
            ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, planTask.eventId!!)

        val intent = Intent(Intent.ACTION_EDIT).apply {
            this.data = uri
            putExtra(CalendarContract.Events._ID, planTask.eventId!!)
            putExtra(CalendarContract.Events.TITLE, task.name)
            putExtra(CalendarContract.Events.DESCRIPTION, task.description)
            putExtra(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, planTask.getTaskEventStartTime())
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, planTask.getTaskEventEndTime())
        }
        createCalenderEvent.launch(intent)
    }

    override fun onDeleteClicked(taskId: Long) {
        viewModel.deletePlannedTask(taskId)
    }

    override fun onMoveToNextDayClicked(taskId: Long) {
        TODO("Not yet implemented")
    }

    private fun onDateSelected(dateWarper: DateWarper, position: Int? = null) {
        if (position != null) {
            Handler(Looper.myLooper()!!).postDelayed(
                { binding.dates.scrollToPosition(position) },200)
        }

        lifecycleScope.launch {
            viewModel.selectedDate.emit(dateWarper.date)
        }

    }
}
