package com.goalmaster.goal.view.goallist

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import com.goalmaster.GoalOptionImpl
import com.goalmaster.R
import com.goalmaster.databinding.FragmentGoalListBinding
import com.goalmaster.databinding.UpdateProgressLayoutBinding
import com.goalmaster.goal.GoalItemTouchHelper
import com.goalmaster.goal.GoalListAdapter
import com.goalmaster.goal.data.entity.GoalData
import com.goalmaster.goal.data.entity.GoalState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch


@AndroidEntryPoint
class GoalListFragment : Fragment(), GoalOptionImpl, AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentGoalListBinding

    private lateinit var uplBinding: UpdateProgressLayoutBinding

    private val viewModel: GoalListViewModel by viewModels()

    private lateinit var mAdapter: GoalListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGoalListBinding
            .inflate(inflater, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.createGoalButton.setOnClickListener {
            val action = GoalListFragmentDirections.actionGoalListFragmentToCreateGoalFragment()
            findNavController().navigate(action)
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.goal_states_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.goalStateSpinner.adapter = adapter
            binding.goalStateSpinner.onItemSelectedListener = this
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.goalList.setHasFixedSize(true)
        mAdapter = GoalListAdapter(this)
        binding.goalList.adapter = mAdapter
        val mDividerItemDecoration = DividerItemDecoration(
            binding.goalList.context,
            DividerItemDecoration.VERTICAL
        )

        val touchHelperCallback = GoalItemTouchHelper(this, mAdapter, binding.goalList)
        val itemTouchHelper = ItemTouchHelper(touchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.goalList)
        binding.goalList.addItemDecoration(mDividerItemDecoration)
        observeGoals()
        binding.goalList.setOnScrollChangeListener { _, _, _, _, _ ->
            mAdapter.closeMenu()
        }
    }

    override fun onResume() {
        super.onResume()
        if (this::binding.isInitialized) {
            setUpBottomNavigation()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeGoals() {
        viewModel.goalList.observe(viewLifecycleOwner) {
            mAdapter.submitList(it.map { g -> GoalData(g) })
        }
    }

    private fun setUpBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.navigationGoals
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigationPlanner -> {
                    val action = GoalListFragmentDirections
                        .actionGoalListFragmentToPlannerFragment()
                    findNavController().navigate(action)
                    true
                }
                else -> false
            }
        }
    }

    override fun openUpdateProgressDialog(id: Long) {
        val action = GoalListFragmentDirections
            .actionGoalListFragmentToViewGoalFragment(id)
        findNavController().navigate(action)
        return
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Update Goal")

        uplBinding = UpdateProgressLayoutBinding
            .inflate(LayoutInflater.from(context), view as ViewGroup?, false)
        uplBinding.goalId = id
        val input = uplBinding.updateGoalValueInput

        builder.setView(uplBinding.root)

        builder.setPositiveButton(R.string.ok) { dialog, _ ->
            dialog.dismiss()
            val updateValue = input.text.toString()
            if (updateValue == "") return@setPositiveButton
            viewModel.updateGoalProgress(id, updateValue.toInt())
        }
        builder.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

    override fun onEditClicked(id: Long) {
        val action = GoalListFragmentDirections
            .actionGoalListFragmentToEditGoalFragment(id)
        findNavController().navigate(action)
    }

    override fun onDeleteClicked(id: Long) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.viewModelScope.launch {
            viewModel.selectedState.emit(if (id == 0L) GoalState.OPENED else GoalState.COMPLETED)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}