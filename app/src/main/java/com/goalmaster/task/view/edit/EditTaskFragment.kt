package com.goalmaster.task.view.edit

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.goalmaster.R
import com.goalmaster.databinding.FragmentEditTaskBinding
import com.goalmaster.utils.observeOnce
import dagger.hilt.android.AndroidEntryPoint
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@AndroidEntryPoint
class EditTaskFragment : Fragment(), TimePickerDialog.OnTimeSetListener {

    private lateinit var timePickerDialog: TimePickerDialog

    private val viewModel: EditTaskViewModel by viewModels()

    private lateinit var binding: FragmentEditTaskBinding

    private val args: EditTaskFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditTaskBinding
            .inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        setUpDurationTimePicker()
        binding.vm = viewModel
        viewModel.start(args.taskId)
        setupListeners()

        return binding.root
    }

    private fun setupListeners() {
        viewModel.saveTaskEvent.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        viewModel.deleteTaskEvent.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
            findNavController().navigateUp()
        }

        binding.editTaskToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.editTaskSaveButton.setOnClickListener {
            viewModel.saveTask()
        }

        binding.durationClearButton.setOnClickListener {
            viewModel.clearDuration()
        }

        binding.editTaskDeleteButton.setOnClickListener {
            viewModel.deleteTask()
        }
    }

    private fun setUpDurationTimePicker() {
        timePickerDialog = TimePickerDialog(
            requireContext(), R.style.DialogTheme,
            this, 3, 0, true
        )
        timePickerDialog.setTitle(R.string.task_duration_picker_dialog_title)

        binding.editTaskDurationInput.inputType = InputType.TYPE_NULL
        binding.editTaskDurationInput.setOnClickListener {
            if (timePickerDialog.isShowing) {
                timePickerDialog.hide()
            }
            else timePickerDialog.show()
        }

        viewModel.duration.observeOnce(viewLifecycleOwner) {
            if (it == null) return@observeOnce
            val hours = it.inWholeHours.toInt()
            val minutes = it.inWholeMinutes.minus(hours.hours.inWholeMinutes).toInt()
            timePickerDialog.updateTime(hours, minutes)
        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val hours = if (hourOfDay > 3) 3 else hourOfDay
        viewModel.duration.value = hours.hours + minute.minutes
    }
}