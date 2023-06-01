package com.goalmaster.plan.view.addtask

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.goalmaster.databinding.FragmentTaskPlannerBinding
import com.goalmaster.task.data.entity.TaskWithData
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@AndroidEntryPoint
class TaskPlannerFragment : Fragment() {

    private lateinit var binding: FragmentTaskPlannerBinding

    private val viewModel: TaskPlannerViewModel by viewModels()

    private lateinit var mAdapter: PlanTaskListAdapter

    lateinit var createCalenderEvent: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskPlannerBinding
            .inflate(inflater, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.vm = viewModel
        mAdapter = PlanTaskListAdapter()
        binding.taskLists.adapter = mAdapter
        viewModel.taskForPlan.observe(viewLifecycleOwner) {
            mAdapter.submitList(it)
        }
        setupListeners()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createCalenderEvent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == 0)
            val data = result.data
        }
    }

    private fun setupListeners() {
        binding.taskPlannerToolbar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.taskPlannerPlanButton.setOnClickListener {
            val taskId = mAdapter.getSelectedTask()
            val taskWithData = viewModel.taskForPlan.value?.find { it.task.id == taskId } ?: return@setOnClickListener
            createCalenderEvent(taskWithData)
        }

    }

    private fun createCalenderEvent(data: TaskWithData) {
        val start = Calendar.getInstance()
        val end = Calendar.getInstance()
        end.add(Calendar.MINUTE, data.task.durationInMin!!.toInt())
        val intent = Intent(Intent.ACTION_EDIT);
        intent.type = "vnd.android.cursor.item/event";
        intent.putExtra(CalendarContract.Events.TITLE, data.task.name);
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
            start)
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
            end)
        intent.putExtra(CalendarContract.Events.ALL_DAY, false);// periodicity
        intent.putExtra(CalendarContract.Events.DESCRIPTION,data.task.description)
        createCalenderEvent.launch(intent, )
    }

}