package com.goalmaster.task.view.create

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.goalmaster.R
import com.goalmaster.databinding.FragmentCreateTaskBinding
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@AndroidEntryPoint
class CreateTaskFragment : Fragment(), OnTimeSetListener, AdapterView.OnItemClickListener {

    private lateinit var timePickerDialog: TimePickerDialog

    private val viewModel: CreateTaskViewModel by viewModels()

    private lateinit var binding: FragmentCreateTaskBinding

    private val args: CreateTaskFragmentArgs by navArgs()

    private val clearString = "------"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateTaskBinding
            .inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel
        viewModel.goalId.value = args.goalId
        setUpDurationTimePicker()

        binding.createTaskToolbar.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.createTaskEvent.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        binding.createGoalSaveButton.setOnClickListener {
            viewModel.createTask()
        }
        setupSpinner()
        binding.copyTaskButton.setOnClickListener {
            viewModel.copyTaskFromSelectedTask()
        }
        return binding.root
    }

    private fun setupSpinner() {
        viewModel.allTasks.observe(viewLifecycleOwner) {
            val taskNames = mutableListOf(clearString).apply { addAll(it.map { t -> t.name }) }
            val adapter = ArrayAdapter(requireContext(), R.layout.list_item, taskNames.toTypedArray())
            (binding.selectTaskInput as? AutoCompleteTextView)?.setAdapter(adapter)
            (binding.selectTaskInput as? AutoCompleteTextView)?.onItemClickListener = this
        }
    }

    private fun setUpDurationTimePicker() {
        timePickerDialog = TimePickerDialog(
            requireContext(), R.style.DialogTheme,
            this, 3, 0, true
        )
        timePickerDialog.setTitle(R.string.task_duration_picker_dialog_title)
        binding.createTaskDurationInput.inputType = InputType.TYPE_NULL
        binding.createTaskDurationInput.setOnClickListener {
            if (timePickerDialog.isShowing) timePickerDialog.hide()
            else timePickerDialog.show()
        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val hours = if (hourOfDay > 3) 3 else hourOfDay
        viewModel.duration.value = hours.hours + minute.minutes
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (view == null) return
        viewModel.viewModelScope.launch {
            val selectedTask = (view as MaterialTextView).text.toString()
            if (selectedTask == clearString) {
                viewModel.selectedTaskCopy = null
            } else {
                viewModel.selectedTaskCopy = selectedTask
            }

        }
    }

}