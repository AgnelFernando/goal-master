package com.goalmaster.goal.view.create

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.goalmaster.R
import com.goalmaster.databinding.FragmentCreateGoalBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.GregorianCalendar
import java.util.Calendar


@AndroidEntryPoint
class CreateGoalFragment : Fragment() {

    private val viewModel: CreateGoalViewModel by viewModels()

    private lateinit var binding: FragmentCreateGoalBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateGoalBinding
            .inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel
        binding.createGoalToolbar.setOnClickListener {
            findNavController().navigateUp()
        }
        setUpDueDatePicker()

        viewModel.createGoalEvent.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        binding.createGoalSaveButton.setOnClickListener {
            viewModel.createGoal()
        }
        return binding.root
    }

    private fun setUpDueDatePicker() {
        var calendar = GregorianCalendar()
        val datePickerDialog =
            DatePickerDialog(this.requireContext(), R.style.ThemeOverlay_AppCompat,
                { _, year, month, dayOfMonth ->
                    calendar = GregorianCalendar(year, month, dayOfMonth)
                    viewModel.dueDate.value = calendar.time
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.setTitle(getString(R.string.due_date_picker_dialog_title))
        datePickerDialog.datePicker.minDate = calendar.time.time

        viewModel.dueDate.observe(viewLifecycleOwner) {
            if (it != null) {
                calendar = GregorianCalendar()
                calendar.time = it
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                datePickerDialog.updateDate(year, month, day)
            }
        }

        binding.createGoalDueDateInput.inputType = InputType.TYPE_NULL
        binding.createGoalDueDateInput.setOnClickListener {
            if (datePickerDialog.isShowing) datePickerDialog.hide()
            else datePickerDialog.show()
        }
    }

}