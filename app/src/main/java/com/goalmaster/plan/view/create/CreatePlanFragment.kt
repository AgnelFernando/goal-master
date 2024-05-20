package com.goalmaster.plan.view.create

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.goalmaster.R
import com.goalmaster.databinding.FragmentCreatePlanBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@AndroidEntryPoint
class CreatePlanFragment : Fragment() {

    private val viewModel: CreatePlanViewModel by viewModels()

    private lateinit var binding: FragmentCreatePlanBinding

    private lateinit var startDatePickerDialog: DatePickerDialog
    private lateinit var startTimePickerDialog: TimePickerDialog

    private lateinit var endDatePickerDialog: DatePickerDialog
    private lateinit var endTimePickerDialog: TimePickerDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatePlanBinding
            .inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel
        setupListeners()
        setupPickers()
        return binding.root
    }

    private fun setupListeners() {
        binding.createPlanToolbar.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.createPlanEvent.observe(viewLifecycleOwner) {
            createNotifications()
            findNavController().navigateUp()
        }

        binding.createPlanSaveButton.setOnClickListener {
            viewModel.createPlan()
        }
    }

    private fun createNotifications() {
        var date = LocalDateTime.of(viewModel.startDate.value!!.toLocalDate(),
            LocalTime.of(23,0))
        val endDate = viewModel.endDate.value!!
        var id = viewModel.plan.id.toInt()
//        while (endDate.isAfter(date)) {
//            id += 1
//            val notificationData = NotificationData(id,
//                "Plan & Review",
//                "Let's Review today's task and reviews tomorrow",
//                date)
//            RemindersManager.startReminder(this.requireContext(), notificationData)
//            date = date.plusDays(1)
//        }
    }

    private fun setupPickers() {
        setUpStartDatePicker()
        setUpStartTimePicker()
        setUpEndDatePicker()
        setUpEndTimePicker()
    }

    private fun setUpStartDatePicker() {
        val dt = LocalDateTime.now()
        startDatePickerDialog =
            DatePickerDialog(
                requireContext(),
                this::onStartDateSet,
                dt.year,
                dt.monthValue - 1,
                dt.dayOfMonth
            )
        startDatePickerDialog.setTitle(getString(R.string.start_date_picker_dialog_title))

        viewModel.startDate.observe(viewLifecycleOwner) {
            if (it != null) {
                startDatePickerDialog.updateDate(it.year, it.monthValue - 1, it.dayOfMonth)
            }
        }

        binding.createPlanStartDateInput.inputType = InputType.TYPE_NULL
        binding.createPlanStartDateInput.setOnClickListener {
            if (startDatePickerDialog.isShowing) startDatePickerDialog.hide()
            else startDatePickerDialog.show()
        }
    }

    private fun setUpStartTimePicker() {
        val dt = LocalDateTime.now()
        startTimePickerDialog = TimePickerDialog(
            requireContext(),
            this::onStartTimeSet, dt.hour + 2, dt.minute, false
        )
        startTimePickerDialog.setTitle(getString(R.string.start_time_picker_dialog_title))

        viewModel.startDate.observe(viewLifecycleOwner) {
            if (it != null) {
                startTimePickerDialog.updateTime(it.hour, it.minute)
            }
        }

        binding.createPlanStartTimeInput.inputType = InputType.TYPE_NULL
        binding.createPlanStartTimeInput.setOnClickListener {
            if (startTimePickerDialog.isShowing) startTimePickerDialog.hide()
            else startTimePickerDialog.show()
        }
    }

    private fun setUpEndDatePicker() {
        val dt = LocalDateTime.now().plusDays(7)
        endDatePickerDialog =
            DatePickerDialog(
                requireContext(),
                this::onEndDateSet,
                dt.year,
                dt.monthValue - 1,
                dt.dayOfMonth
            )
        endDatePickerDialog.setTitle(getString(R.string.end_date_picker_dialog_title))

        viewModel.endDate.observe(viewLifecycleOwner) {
            if (it != null) {
                endDatePickerDialog.updateDate(it.year, it.monthValue - 1, it.dayOfMonth)
            }
        }

        binding.createPlanEndDateInput.inputType = InputType.TYPE_NULL
        binding.createPlanEndDateInput.setOnClickListener {
            if (endDatePickerDialog.isShowing) endDatePickerDialog.hide()
            else endDatePickerDialog.show()
        }
    }

    private fun setUpEndTimePicker() {
        val dt = LocalDateTime.now().plusDays(7)
        endTimePickerDialog = TimePickerDialog(requireContext(), this::onEndTimeSet,
            dt.hour, dt.minute, false
        )
        endTimePickerDialog.setTitle(getString(R.string.end_time_picker_dialog_title))

        viewModel.endDate.observe(viewLifecycleOwner) {
            if (it != null) {
                endTimePickerDialog.updateTime(it.hour, it.minute)
            }
        }

        binding.createPlanEndTimeInput.inputType = InputType.TYPE_NULL
        binding.createPlanEndTimeInput.setOnClickListener {
            if (endTimePickerDialog.isShowing) endTimePickerDialog.hide()
            else endTimePickerDialog.show()
        }
    }

    private fun onStartDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        if (view == null) return
        val date = LocalDate.of(year, month+1, dayOfMonth)

        if (viewModel.startDate.value == null)
            viewModel.startDate.value = LocalDateTime.now().plusHours(2)

        val value = viewModel.startDate.value!!
        viewModel.startDate.value = LocalDateTime.of(date, value.toLocalTime())

    }

    private fun onEndDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        if (view == null) return
        val date = LocalDate.of(year, month+1, dayOfMonth)

        if (viewModel.endDate.value == null)
            viewModel.endDate.value = LocalDateTime.now()

        val value = viewModel.endDate.value!!
        viewModel.endDate.value = LocalDateTime.of(date, value.toLocalTime())

    }

    private fun onStartTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        if (view == null) return
        val time = LocalTime.of(hourOfDay, minute)

        if (viewModel.startDate.value == null)
            viewModel.startDate.value = LocalDateTime.now().plusHours(2)

        val value = viewModel.startDate.value!!
        viewModel.startDate.value = LocalDateTime.of(value.toLocalDate(), time)
    }

    private fun onEndTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        if (view == null) return
        val time = LocalTime.of(hourOfDay, minute)

        if (viewModel.endDate.value == null)
            viewModel.endDate.value = LocalDateTime.now()

        val value = viewModel.endDate.value!!
        viewModel.endDate.value = LocalDateTime.of(value.toLocalDate(), time)
    }

}