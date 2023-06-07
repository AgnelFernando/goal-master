package com.goalmaster.plan.view.addtask

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.CalendarContract.Calendars
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.goalmaster.databinding.FragmentTaskPlannerBinding
import com.goalmaster.task.data.entity.TaskWithData
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@RequiresApi(Build.VERSION_CODES.Q)
@AndroidEntryPoint
class TaskPlannerFragment : Fragment(), AsyncQueryListener {

    private var lastCalId: Long = 0

    private lateinit var binding: FragmentTaskPlannerBinding

    private val viewModel: TaskPlannerViewModel by viewModels()

    private lateinit var mAdapter: PlanTaskListAdapter

    lateinit var createCalenderEvent: ActivityResultLauncher<Intent>

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts
            .RequestMultiplePermissions()
    ) {
        it.isEmpty()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskPlannerBinding
            .inflate(inflater, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.vm = viewModel
        mAdapter = PlanTaskListAdapter()
        binding.taskLists.adapter = mAdapter
        observeData()
        setupListeners()
        viewModel.setup()
        return binding.root
    }

    private fun observeData() {
        viewModel.tasksForPlan.observe(viewLifecycleOwner) {
            mAdapter.submitList(it)
        }
        viewModel.createPlanTaskEvent.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }

    private fun setupListeners() {
        binding.taskPlannerToolbar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.taskPlannerPlanButton.setOnClickListener {
            viewModel.taskId = mAdapter.getSelectedTask()
            val task = viewModel.createTaskPlan(mAdapter.getSelectedTask())
//            createCalenderEventPlaceHolder(task)
            createCalenderEvent(task)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createCalenderEvent = registerForActivityResult(ActivityResultContracts
            .StartActivityForResult()) {
            updateTaskPlanEventTime()
        }

        if (!haveCalendarPermission()) {
            requestPermissionLauncher
                .launch(arrayOf(Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR))
        }

        updateCalenderId()
    }

    private fun updateTaskPlanEventTime() {
        val qh = QueryHandler(requireContext().contentResolver, this)
//        qh.startQuery(101, )
        viewModel.saveSelectedPlanTask()
    }

    private fun haveCalendarPermission(): Boolean {
        val check = ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.WRITE_CALENDAR)
        return check == PackageManager.PERMISSION_GRANTED
    }

    private fun updateCalenderId() {
        val EVENT_PROJECTION: Array<String> = arrayOf(
            Calendars._ID,                     // 0
            Calendars.CALENDAR_DISPLAY_NAME,   // 2
        )

//
        val cr = requireContext().contentResolver
        val cur = cr.query(
            Calendars.CONTENT_URI,
            EVENT_PROJECTION,
            null,
            null,
            null
        )!!

        while (cur.moveToNext()) {
            val calID = cur.getLong(0)
            val displayName: String? = cur.getString(1)
            if (displayName == "gilesagnel@gmail.com") {
                lastCalId = calID
            }
        }
        cur.close()
    }
    fun getEventIdList(contentResolver: ContentResolver){
//        val eventIdList = ArrayList<Long>()
//        val eventTitles = ArrayList<String>()
//
//        val EVENT_PROJECTION: Array<String> = arrayOf(
//            CalendarContract.Events._ID, // 0
//            CalendarContract.Events.TITLE  // 1
//        )
//        val PROJECTION_EVENT_ID_INDEX: Int = 0
//        val PROJECTION_TITLE_INDEX: Int = 1
//
//        contentResolver.query(CalendarContract.Events.CONTENT_URI, arrayOf(), "", arrayOf(), null)
//            ?.let {
//                while (it.moveToNext() ?: false) {
//                    // Get the field values
//                    val eventId = it.getLong(PROJECTION_EVENT_ID_INDEX)
//                    val title = it.getString(PROJECTION_TITLE_INDEX)
//                    eventIdList.add(eventId)
//                    eventTitles.add(title)
//                }
//            }
//        return eventIdList
    }

    private fun createCalenderEventPlaceHolder(task: TaskWithData) {

        val sm = viewModel.getTaskEventStartTime()
        val em = viewModel.getTaskEventEndTime()

        val initialValues = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, sm)
            put(CalendarContract.Events.DTEND, em)
            put(CalendarContract.Events.TITLE, task.goal.name + ":" + task.task.name)
            put(CalendarContract.Events.DESCRIPTION, task.task.description)
            put(CalendarContract.Events.CALENDAR_ID, lastCalId)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }
        val uri = CalendarContract.Events.CONTENT_URI.buildUpon()
            .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
            .appendQueryParameter(Calendars.ACCOUNT_NAME, "gilesagnel@gmail.com")
            .appendQueryParameter(Calendars.ACCOUNT_TYPE, "com.google").build()

        val qh = QueryHandler(requireActivity().contentResolver, this)
        qh.startInsert(-1, null, uri, initialValues)

        val values = ContentValues()
        values.put(Calendars.SYNC_EVENTS, 1)
        values.put(Calendars.VISIBLE, 1)
        values.put(Calendars._ID, lastCalId)
        val uri1 = ContentUris.withAppendedId(Calendars.CONTENT_URI, lastCalId)
        qh.startUpdate(100, null, uri1,
            values, null, null)
    }

    private fun createCalenderEvent(task: TaskWithData) {
        val planTask = viewModel.planTask.value ?: return

        val uri: Uri =
            ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, planTask.eventId!!)

        val intent = Intent(Intent.ACTION_EDIT).apply {
            data = uri
            putExtra(CalendarContract.Events.CALENDAR_ID, lastCalId)
            putExtra(CalendarContract.Events._ID, planTask.eventId!!)
            putExtra(CalendarContract.Events.TITLE, task.task.name)
            putExtra(CalendarContract.Events.DESCRIPTION, task.task.description)
            putExtra(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, viewModel.getTaskEventStartTime())
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, viewModel.getTaskEventEndTime())
        }
//            .putExtra(CalendarContract.Events.TITLE, "Foo New Title")
//        val start = Calendar.getInstance()
//        val end = Calendar.getInstance()
//        end.add(Calendar.MINUTE, data.task.durationInMin!!.toInt())
//        val intent = Intent(Intent.ACTION_EDIT)
//        intent.type = "vnd.android.cursor.item/event"
//        intent.putExtra(CalendarContract.EXTRA_EVENT_ID, planTask.eventId)
//        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
//            start)
//        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
//            end)
//        intent.putExtra(CalendarContract.Events.ALL_DAY, false)// periodicity
//        intent.putExtra(CalendarContract.Events.DESCRIPTION,data.task.description)
        createCalenderEvent.launch(intent)
    }

    override fun onCrInsertComplete(eventId: Long) {
        viewModel.updateEventId(eventId)
    }

    override fun onCrUpdateComplete() {
//        createCalenderEvent(nu)
    }

}
