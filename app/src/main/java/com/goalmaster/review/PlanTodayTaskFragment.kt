package com.goalmaster.review

import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.goalmaster.databinding.FragmentPlanTodayTaskBinding
import com.goalmaster.plan.view.planner.PlanTaskOptionImpl
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PlanTodayTaskFragment : Fragment(), PlanTaskOptionImpl {

    private lateinit var binding: FragmentPlanTodayTaskBinding

    private lateinit var mAdapter: TaskReviewAdapter

    private val viewModel: PlanTodayTasksViewModel by viewModels()

    lateinit var createCalenderEvent: ActivityResultLauncher<Intent>

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
        binding = FragmentPlanTodayTaskBinding
            .inflate(inflater, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.planTodayTasksToolbar.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.planTodayTasksDoneButton.setOnClickListener {
            val action = PlanTodayTaskFragmentDirections
                .actionPlanTodayTaskFragmentToPlannerFragment()
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

    override fun onDoneClicked(taskId: Long) {}
    override fun onMoveToNextDayClicked(taskId: Long) {}
    override fun onViewClicked(taskId: Long) {}
    override fun onDeleteClicked(taskId: Long) {}
}