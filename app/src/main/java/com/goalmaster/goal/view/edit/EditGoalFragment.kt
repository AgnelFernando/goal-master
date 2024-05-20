package com.goalmaster.goal.view.edit

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.goalmaster.R
import com.goalmaster.databinding.FragmentEditGoalBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.GregorianCalendar

@AndroidEntryPoint
class EditGoalFragment : Fragment() {

    private val viewModel: EditGoalViewModel by viewModels()

    private lateinit var binding: FragmentEditGoalBinding

    private val args: EditGoalFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditGoalBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel
        viewModel.start(args.goalId)
        setupListeners()
        setUpDueDatePicker()
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

        binding.editGoalDueDateInput.inputType = InputType.TYPE_NULL
        binding.editGoalDueDateInput.setOnClickListener {
            if (datePickerDialog.isShowing) datePickerDialog.hide()
            else datePickerDialog.show()
        }
    }


    private fun setupListeners() {
        viewModel.saveGoalEvent.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        viewModel.deleteGoalEvent.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
            findNavController().navigateUp()
        }

        binding.editGoalToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.editGoalSaveButton.setOnClickListener {
            viewModel.saveGoal()
        }

        binding.editGoalDeleteButton.setOnClickListener {
            viewModel.deleteGoal()
        }

        binding.editGoalArchiveButton.setOnClickListener {
            viewModel.archiveGoal()
        }
    }

}