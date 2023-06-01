package com.goalmaster.goal.view.goallist

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class GoalListFragment : Fragment(), GoalOptionImpl {

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
        lifecycleScope.launchWhenCreated {
            viewModel.goalList.collectLatest {
                mAdapter.submitList(it.map { g -> GoalData(g) })
            }
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

}