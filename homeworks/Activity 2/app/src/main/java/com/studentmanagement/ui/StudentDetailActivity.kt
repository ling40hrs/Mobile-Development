package com.studentmanagement.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.studentmanagement.R
import com.studentmanagement.data.StudentRepository
import com.studentmanagement.databinding.ActivityStudentDetailBinding
import com.studentmanagement.model.Student
import com.studentmanagement.model.Subject
import com.studentmanagement.ui.adapter.EnrollmentAdapter
import com.studentmanagement.ui.dialog.GradeDialog
import com.studentmanagement.ui.dialog.SubjectEnrollmentDialog
import com.studentmanagement.util.toast
import com.studentmanagement.util.toGradeText

class StudentDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_STUDENT_ID = "extra_student_id"
        private const val UNKNOWN_ID = -1
    }

    private lateinit var binding: ActivityStudentDetailBinding
    private lateinit var adapter: EnrollmentAdapter
    private var studentId: Int = UNKNOWN_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }
        studentId = intent.getIntExtra(EXTRA_STUDENT_ID, UNKNOWN_ID)
        adapter = EnrollmentAdapter { subject -> openGradeDialog(subject) }
        binding.recyclerEnrollments.adapter = adapter
        binding.buttonEnroll.setOnClickListener { openEnrollDialog() }
        binding.buttonEditStudent.setOnClickListener { openEditForm() }
        binding.buttonDeleteStudent.setOnClickListener { confirmDelete() }
    }

    override fun onResume() {
        super.onResume()
        render()
    }

    private fun currentStudent(): Student? = StudentRepository.findById(studentId)

    private fun render() {
        val student = currentStudent()
        if (student == null) {
            toast(getString(R.string.student_not_found))
            finish()
            return
        }
        binding.textDetailAvatar.text = student.avatarInitial
        binding.textDetailName.text = student.fullName
        binding.textDetailNumber.text = student.studentNumber
        binding.textDetailMeta.text = getString(
            R.string.student_card_meta,
            student.yearLevel.label,
            student.gender.label,
            getString(R.string.subjects_count, student.enrollments.size)
        )
        binding.textDetailEmail.text = student.email
        binding.textDetailAge.text = student.age.toString()
        binding.textDetailContact.text = student.contactNumber
        binding.textDetailGender.text = student.gender.label
        binding.textDetailYear.text = student.yearLevel.label
        binding.textSubjectCount.text = student.enrollments.size.toString()
        binding.textAverageValue.text = student.averageGrade?.toGradeText()
            ?: getString(R.string.value_not_available)
        val hasEnrollments = student.enrollments.isNotEmpty()
        binding.recyclerEnrollments.isVisible = hasEnrollments
        binding.layoutNoSubjects.isVisible = !hasEnrollments
        if (hasEnrollments) {
            adapter.submit(student.enrollments.toList())
        }
    }

    private fun openEnrollDialog() {
        val student = currentStudent() ?: return
        SubjectEnrollmentDialog(this, student) {
            toast(getString(R.string.enrolled_success))
            render()
        }.show()
    }

    private fun openGradeDialog(subject: Subject) {
        val student = currentStudent() ?: return
        GradeDialog(this, student, preselected = subject) {
            toast(getString(R.string.grade_saved_success))
            render()
        }.show()
    }

    private fun openEditForm() {
        val student = currentStudent() ?: return
        val intent = Intent(this, StudentFormActivity::class.java)
        intent.putExtra(StudentFormActivity.EXTRA_STUDENT_ID, student.id)
        startActivity(intent)
    }

    private fun confirmDelete() {
        val student = currentStudent() ?: return
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_title)
            .setMessage(getString(R.string.delete_message, student.fullName))
            .setPositiveButton(R.string.remove) { _, _ ->
                if (StudentRepository.remove(student.id)) {
                    toast(getString(R.string.deleted_success))
                    finish()
                } else {
                    toast(getString(R.string.student_not_found))
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
