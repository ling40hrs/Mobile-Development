package com.studentmanagement.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.studentmanagement.R
import com.studentmanagement.data.SortOption
import com.studentmanagement.data.StudentFilter
import com.studentmanagement.data.StudentQuery

class SortFilterDialog(
    private val context: Context,
    private val current: StudentQuery,
    private val onApply: (StudentQuery) -> Unit
) {

    fun show() {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_sort_filter, null)
        val sortGroup = view.findViewById<RadioGroup>(R.id.radioGroupSort)
        val filterGroup = view.findViewById<RadioGroup>(R.id.radioGroupFilter)

        SortOption.entries.forEach { option ->
            sortGroup.addView(createOptionButton(option.label, option, option == current.sort))
        }
        StudentFilter.options().forEach { filter ->
            filterGroup.addView(createOptionButton(filter.label, filter, filter == current.filter))
        }

        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.sort_and_filter)
            .setView(view)
            .setPositiveButton(R.string.apply) { _, _ ->
                val sort = selected<SortOption>(sortGroup) ?: current.sort
                val filter = selected<StudentFilter>(filterGroup) ?: current.filter
                onApply(current.copy(sort = sort, filter = filter))
            }
            .setNeutralButton(R.string.reset) { _, _ ->
                onApply(current.copy(sort = SortOption.NAME_ASC, filter = StudentFilter.All))
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun createOptionButton(label: String, value: Any, checked: Boolean): RadioButton {
        return RadioButton(context).apply {
            id = View.generateViewId()
            text = label
            tag = value
            isChecked = checked
            textSize = 14f
            setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            val padding = (12 * context.resources.displayMetrics.density).toInt()
            setPadding(padding, padding, padding, padding)
        }
    }

    private inline fun <reified T> selected(group: RadioGroup): T? {
        val checkedId = group.checkedRadioButtonId
        if (checkedId == View.NO_ID) {
            return null
        }
        val checked = group.findViewById<RadioButton>(checkedId) ?: return null
        val value = checked.tag
        return if (value is T) value else null
    }
}
