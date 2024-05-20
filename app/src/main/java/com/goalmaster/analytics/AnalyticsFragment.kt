package com.goalmaster.analytics

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.goalmaster.R
import com.goalmaster.databinding.FragmentAnalyticsBinding
import com.goalmaster.plan.data.entity.PlanTaskStatus
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AnalyticsFragment : Fragment(), OnChartValueSelectedListener {

    private lateinit var binding: FragmentAnalyticsBinding

    private val viewModel: AnalyticsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnalyticsBinding
            .inflate(inflater, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.vm = viewModel
        setUpBottomNavigation()
        setupChart()
        setupProgressChart()
        observePlannedGoals()
        return binding.root
    }

    private fun setupProgressChart() {
        val chart = binding.plannedGoalsProgress
        chart.setOnChartValueSelectedListener(this)
        chart.setDrawBarShadow(false)
        chart.description.isEnabled = false

//        chart.setMaxVisibleValueCount(60)

        chart.setPinchZoom(false)
        chart.setDrawBarShadow(true)
        chart.setDrawGridBackground(false)

        val xl: XAxis = chart.xAxis
        xl.position = XAxisPosition.BOTTOM
        xl.setDrawAxisLine(false)
        xl.setDrawGridLines(false)
        xl.setDrawLabels(false)
        xl.axisMinimum = 0f


        val yl: YAxis = chart.axisLeft
        yl.setDrawAxisLine(false)
        yl.setDrawGridLines(false)
        yl.axisMinimum = 0f
        yl.axisMaximum = 100f


        val yr: YAxis = chart.axisRight
        yr.setDrawAxisLine(false)
        yr.setDrawGridLines(false)
        yr.axisMinimum = 0f

        chart.animateY(1500)

        setBarChartData()

        val l: Legend = chart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.xEntrySpace = 8f
        l.yEntrySpace = 8f

    }

    private fun setupChart() {
        val chart = binding.plannedGoals
        chart.setUsePercentValues(true)
        chart.description.isEnabled = true
        chart.description.text = "Next week planned Goals "
        chart.description.textSize = 12f
        chart.setExtraOffsets(5f, 10f, 5f, 5f)
        chart.dragDecelerationFrictionCoef = 0.95f
        chart.centerText = generateCenterSpannableText()
        chart.isDrawHoleEnabled = true
        chart.setHoleColor(Color.WHITE)
        chart.setTransparentCircleColor(Color.WHITE)
        chart.setTransparentCircleAlpha(110)
        chart.holeRadius = 58f
        chart.transparentCircleRadius = 61f
        chart.setDrawCenterText(true)
        chart.rotationAngle = 0f
        chart.isRotationEnabled = true
        chart.isHighlightPerTapEnabled = true
        // add a selection listener
        chart.setOnChartValueSelectedListener(this)

        chart.animateY(1400, Easing.EaseInOutQuad)

        // entry label styling
        chart.setEntryLabelColor(Color.GRAY)
        chart.setEntryLabelTextSize(12f)
    }

    private fun generateCenterSpannableText(): SpannableString {
        val s = SpannableString("Planned Goals")
        s.setSpan(RelativeSizeSpan(1.7f), 0, s.length, 0)
        return s
    }

    private fun setUpBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.navigationAnalytics
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigationGoals -> {
                    val action =
                        AnalyticsFragmentDirections.actionAnalyticsFragmentToGoalListFragment()
                    findNavController().navigate(action)
                    true
                }
                R.id.navigationPlanner -> {
                    val action =
                        AnalyticsFragmentDirections.actionAnalyticsFragmentToPlannerFragment()
                    findNavController().navigate(action)
                    true
                }
//                R.id.navigationSettings -> {
//                    val action = AnalyticsFragmentDirections
//                            .actionAnalyticsFragmentToSettingFragment()
//                    findNavController().navigate(action)
//                    true
//                }
                else -> false
            }
        }
    }

    private fun observePlannedGoals() {
        viewModel.plannedGoals.observe(viewLifecycleOwner) {
            val entries = it.map { (goal, duration) -> PieEntry(duration.toFloat(), goal.name) }
            val dataSet = PieDataSet(entries, "")
            dataSet.setDrawIcons(false)
            dataSet.sliceSpace = 3f
            dataSet.iconsOffset = MPPointF(0f, 40f)
            dataSet.selectionShift = 5f

            // add a lot of colors
            dataSet.colors = ArrayList<Int>().apply {
                addAll(ColorTemplate.VORDIPLOM_COLORS.toList())
                addAll(ColorTemplate.JOYFUL_COLORS.toList())
                addAll(ColorTemplate.COLORFUL_COLORS.toList())
                addAll(ColorTemplate.LIBERTY_COLORS.toList())
                addAll(ColorTemplate.PASTEL_COLORS.toList())
                add(ColorTemplate.getHoloBlue())
            }
            val data = PieData(dataSet)
            data.setValueFormatter(PercentFormatter())
            data.setValueTextSize(11f)
            data.setValueTextColor(Color.GRAY)
            binding.plannedGoals.data = data

            // undo all highlights
            binding.plannedGoals.highlightValues(null)
            binding.plannedGoals.invalidate()
        }
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        if (e == null) return
        Log.i(
            "VAL SELECTED",
            "Value: " + e.y + ", index: " + h!!.x
                    + ", DataSet index: " + h.dataSetIndex
        )
    }

    override fun onNothingSelected() {
        Log.i("PieChart", "nothing selected")
    }

    private fun setBarChartData() {
        viewModel.plannedTasksGoals.observe(viewLifecycleOwner) {
            val taskDone = HashMap<String, Float>()
            val allTasks = HashMap<String, Float>()
            it.forEach { t ->
                val gname = t.goal.name
                if (t.planTask?.status == PlanTaskStatus.COMPLETED){
                    taskDone[gname] = taskDone.getOrDefault(gname, 0f) + 1f
                }
                allTasks[gname] = allTasks.getOrDefault(gname, 0f) + 1f
            }
            taskDone["All"] = taskDone.values.sum()
            allTasks["All"] = allTasks.values.sum()
            taskDone.replaceAll { k, v -> (v / allTasks[k]!!) * 100f  }
            var values: ArrayList<BarEntry>
            var i = 0
            val spaceForBar = 0.25f
            val barWidth = 0.2f
            val dataSets = ArrayList<IBarDataSet>()
            val colors = ColorTemplate.COLORFUL_COLORS.toList()
            taskDone.forEach { (k, v) ->
                values = arrayListOf(BarEntry(i++ * spaceForBar + barWidth/2, v))
                val barDataSet = BarDataSet(values, k)
                barDataSet.color = colors[i]
                dataSets.add(barDataSet)
            }

            val data = BarData(dataSets)
            data.setValueTextSize(10f)
            data.barWidth = barWidth
            binding.plannedGoalsProgress.data = data
        }
    }


}

