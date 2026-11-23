package com.studentmanagement.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.studentmanagement.R
import com.studentmanagement.model.Student
import com.studentmanagement.model.Subject
import com.studentmanagement.util.toast
import com.studentmanagement.util.toGradeText
import com.studentmanagement.util.value
import com.studentmanagement.validation.Validation
import com.studentmanagement.validation.Validator

class GradeDialog(
    private val context: Context,
    private val student: Student,
    private val preselected: Subject? = null,
    private val onGradeSaved: () -> Unit
) {

    fun show() {
        if (student.enrollments.isEmpty()) {
            context.toast(context.getString(R.string.err_no_enrollments))
            return
        }

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_grade, null)
        val group = view.findViewById<RadioGroup>(R.id.radioGroupSubjects)
        val gradeLayout = view.findViewById<TextInputLayout>(R.id.inputLayoutGrade)
        val gradeInput = view.findViewById<TextInputEditText>(R.id.editGrade)

        val buttons = student.enrollments.map { enrollment ->
            RadioButton(context).apply {
                id = View.generateViewId()
                text = "${enrollment.subject.code} - ${enrollment.subject.title}"
                tag = enrollment.subject
                textSize = 14f
                setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                group.addView(this)
            }
        }

        val target = preselected?.takeIf { student.isEnrolledIn(it) }
            ?: student.enrollments.firstOrNull { !it.isGraded }?.subject
            ?: student.enrollments.first().subject
        buttons.firstOrNull { it.tag as? Subject == target }?.isChecked = true
        gradeInput.setText(student.enrollmentOf(target)?.grade?.toGradeText().orEmpty())

        group.setOnCheckedChangeListener { radioGroup, checkedId ->
            val subject = selectedSubject(radioGroup, checkedId)
            gradeInput.setText(subject?.let { student.enrollmentOf(it)?.grade }?.toGradeText().orEmpty())
            gradeLayout.error = null
        }

        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(R.string.add_grade)
            .setView(view)
            .setPositiveButton(R.string.save, null)
            .setNegativeButton(R.string.cancel, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val subject = selectedSubject(group, group.checkedRadioButtonId)
                if (subject == null) {
                    gradeLayout.error = context.getString(R.string.err_subject_required)
                    return@setOnClickListener
                }
                when (val result = Validator.validateGrade(gradeInput.value())) {
                    is Validation.Success -> {
                        student.setGrade(subject, result.value)
                        onGradeSaved()
                        dialog.dismiss()
                    }

                    is Validation.Failure -> {
                        val message = result.errors.firstOrNull()?.messageRes
                        gradeLayout.error = message?.let { context.getString(it) }
                    }
                }
            }
        }
        dialog.show()
    }

    private fun selectedSubject(group: RadioGroup, checkedId: Int): Subject? {
        if (checkedId == View.NO_ID) {
            return null
        }
        val checked = group.findViewById<RadioButton>(checkedId) ?: return null
        return checked.tag as? Subject
    }
}
