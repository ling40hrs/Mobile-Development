package com.studentmanagement.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.studentmanagement.R
import com.studentmanagement.model.Student
import com.studentmanagement.model.Subject
import com.studentmanagement.util.toast
import com.studentmanagement.validation.Validation
import com.studentmanagement.validation.Validator

class SubjectEnrollmentDialog(
    private val context: Context,
    private val student: Student,
    private val onEnrolled: (List<Subject>) -> Unit
) {

    fun show() {
        val available = student.availableSubjects()
        if (available.isEmpty()) {
            context.toast(context.getString(R.string.err_all_enrolled))
            return
        }

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_enroll_subject, null)
        val container = view.findViewById<LinearLayout>(R.id.containerSubjects)
        val error = view.findViewById<TextView>(R.id.textEnrollError)

        val checkBoxes = available.map { subject ->
            MaterialCheckBox(context).apply {
                text = "${subject.code} - ${subject.title}"
                tag = subject
                textSize = 14f
                container.addView(this)
            }
        }

        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(R.string.enroll_subject)
            .setView(view)
            .setPositiveButton(R.string.enroll_subject, null)
            .setNegativeButton(R.string.cancel, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val selected = checkBoxes
                    .filter { it.isChecked }
                    .mapNotNull { it.tag as? Subject }
                when (val result = Validator.validateEnrollment(selected, available)) {
                    is Validation.Success -> {
                        result.value.forEach { student.enroll(it) }
                        onEnrolled(result.value)
                        dialog.dismiss()
                    }

                    is Validation.Failure -> {
                        val message = result.errors.firstOrNull()?.messageRes
                        if (message != null) {
                            error.text = context.getString(message)
                            error.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
        dialog.show()
    }
}
