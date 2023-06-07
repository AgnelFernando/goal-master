package com.goalmaster

import android.content.Context
import android.text.format.DateUtils
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


@BindingAdapter("android:numberInput")
fun bindNumberInput(view: TextView, data: Long) {
    view.text = data.toString()
}

@BindingAdapter("android:dateInput")
fun bindDateInput(view: TextInputEditText, data: Date?) {
    data?.let {
        val format = SimpleDateFormat("MMM-dd-yyyy", Locale.getDefault())
        view.setText(format.format(it))
    }
}

@BindingAdapter("android:dateInput")
fun bindDateInput(view: TextInputEditText, data: LocalDate?) {
    data?.let {
        val text = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(it)
        view.setText(text)
    }
}

@BindingAdapter("android:timeInput")
fun bindTimeInput(view: TextInputEditText, data: LocalTime?) {
    data?.let {
        val text = it.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
        view.setText(text)
    }
}

@BindingAdapter("android:dateText")
fun bindDateText(view: TextView, data: Date?) {
    data?.let {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        view.text = format.format(it)
    }
}

@BindingAdapter("show")
fun bindShow(mediaView: View, show: Boolean) {
    mediaView.visibility = if (show) {
        View.VISIBLE
    } else {
        View.GONE
    }
}
