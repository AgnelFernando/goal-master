package com.goalmaster.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.goalmaster.R
import com.goalmaster.databinding.FragmentSettingBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding

    private val viewModel: SettingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding
            .inflate(inflater, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.vm = viewModel
        setUpBottomNavigation()
        return binding.root
    }

    private fun setUpBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.navigationSettings
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
//                R.id.navigationGoals -> {
//                    val action =
//                        SettingFragmentDirections.actionSettingFragmentToGoalListFragment()
//                    findNavController().navigate(action)
//                    true
//                }
//                R.id.navigationPlanner -> {
//                    val action =
//                        SettingFragmentDirections.actionSettingFragmentToPlannerFragment()
//                    findNavController().navigate(action)
//                    true
//                }
//                R.id.navigationAnalytics -> {
//                    val action =
//                        SettingFragmentDirections.actionSettingFragmentToAnalyticsFragment()
//                    findNavController().navigate(action)
//                    true
//                }
                else -> false
            }
        }
    }

}

