package com.goalmaster.plan.view.addtask

import android.app.DatePickerDialog
import android.content.*
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.goalmaster.R
import com.goalmaster.databinding.FragmentTaskPlannerBinding
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*


@RequiresApi(Build.VERSION_CODES.Q)
@AndroidEntryPoint
class TaskPlannerFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentTaskPlannerBinding

    private val viewModel: TaskPlannerViewModel by viewModels()

    private lateinit var mAdapter: PlanTaskListAdapter

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
        observeData()
        setupListeners()
        viewModel.setup()
        setUpDatePicker()
        setupSpinner()
        return binding.root
    }

    private fun setupSpinner() {
        viewModel.availableGoals.observe(viewLifecycleOwner) {
            val goalNames = mutableListOf("All").apply { addAll(it.map { g -> g.name }) }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, goalNames.toTypedArray())
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.filterByGoalSpinner.adapter = adapter
            binding.filterByGoalSpinner.onItemSelectedListener = this
        }
    }

    private fun observeData() {
        viewModel.allTasks.observe(viewLifecycleOwner) {}

        viewModel.tasksForPlan.observe(viewLifecycleOwner) {
            mAdapter.selectedPos = -1
            mAdapter.submitList(it)
        }
        viewModel.createPlanTaskEvent.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }

    private fun setupListeners() {
        binding.taskPlannerToolbar.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setUpDatePicker() {
        val calendar = GregorianCalendar()
        val cur = this
        val datePickerDialog =
            DatePickerDialog(this.requireContext(), R.style.ThemeOverlay_AppCompat,
                { _, year, month, dayOfMonth ->
                    val date = LocalDateTime.of(LocalDate.of(year, month + 1, dayOfMonth), LocalTime.now())
                    viewModel.createTaskPlan(cur.mAdapter.getSelectedTask(), date)
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.setTitle(getString(R.string.task_planned_on_title))
        datePickerDialog.datePicker.minDate = calendar.time.time


        binding.taskPlannerPlanButton.setOnClickListener {
//            if (mAdapter.selectedPos = -1)
            if (datePickerDialog.isShowing) datePickerDialog.hide()
            else datePickerDialog.show()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (view == null) return
        viewModel.viewModelScope.launch {
            viewModel.selectedGoal.emit((view as MaterialTextView).text.toString())
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

}
