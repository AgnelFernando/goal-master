package com.goalmaster.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.goalmaster.databinding.FragmentReviewAndPlanBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewAndPlanFragment : Fragment() {

    private lateinit var binding: FragmentReviewAndPlanBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewAndPlanBinding
            .inflate(inflater, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.reviewAndPlanButton.setOnClickListener {
            val action = ReviewAndPlanFragmentDirections
                .actionReviewAndPlanFragmentToReviewTaskFragment()
            findNavController().navigate(action)
        }
        binding.exitButton.setOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }
}