package com.studentmanagement.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.studentmanagement.R
import com.studentmanagement.data.StudentQuery
import com.studentmanagement.data.StudentRepository
import com.studentmanagement.databinding.ActivityStudentListBinding
import com.studentmanagement.model.Student
import com.studentmanagement.ui.adapter.StudentAdapter
import com.studentmanagement.ui.dialog.SortFilterDialog
import com.studentmanagement.util.toast

class StudentListActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FOCUS_SEARCH = "extra_focus_search"
    }

    private lateinit var binding: ActivityStudentListBinding
    private lateinit var adapter: StudentAdapter
    private var query = StudentQuery()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }
        adapter = StudentAdapter(
            onStudentClick = { openDetails(it) },
            onStudentLongClick = { showStudentOptions(it) }
        )
        binding.recyclerStudents.adapter = adapter
        binding.editSearch.addTextChangedListener { text ->
            query = query.copy(search = text?.toString().orEmpty())
            refresh()
        }
        binding.buttonSortFilter.setOnClickListener { openSortFilter() }
        binding.fabAddStudent.setOnClickListener { openForm(null) }
        binding.buttonClearFilters.setOnClickListener {
            query = StudentQuery(search = query.search)
            refresh()
        }
        if (intent.getBooleanExtra(EXTRA_FOCUS_SEARCH, false)) {
            binding.editSearch.requestFocus()
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun openDetails(student: Student) {
        val intent = Intent(this, StudentDetailActivity::class.java)
        intent.putExtra(StudentDetailActivity.EXTRA_STUDENT_ID, student.id)
        startActivity(intent)
    }

    private fun openForm(student: Student?) {
        val intent = Intent(this, StudentFormActivity::class.java)
        if (student != null) {
            intent.putExtra(StudentFormActivity.EXTRA_STUDENT_ID, student.id)
        }
        startActivity(intent)
    }

    private fun openSortFilter() {
        SortFilterDialog(this, query) { updated ->
            query = updated
            refresh()
        }.show()
    }

    private fun refresh() {
        val students = StudentRepository.search(query)
        val total = StudentRepository.size
        adapter.submit(students)
        binding.textStudentCount.text = if (query.isDefault) {
            getString(R.string.students_count, total)
        } else {
            getString(R.string.students_shown, students.size, total)
        }
        val hasResults = students.isNotEmpty()
        binding.recyclerStudents.isVisible = hasResults
        binding.layoutEmptyState.isVisible = !hasResults
        if (!hasResults) {
            if (total == 0) {
                binding.textEmptyTitle.setText(R.string.empty_students_title)
                binding.textEmptySubtitle.setText(R.string.empty_students_message)
            } else {
                binding.textEmptyTitle.setText(R.string.empty_result_title)
                binding.textEmptySubtitle.setText(R.string.empty_result_message)
            }
            binding.buttonClearFilters.isVisible = !query.isDefault
        }
    }

    private fun showStudentOptions(student: Student) {
        val options = arrayOf(
            getString(R.string.option_view),
            getString(R.string.edit),
            getString(R.string.delete)
        )
        MaterialAlertDialogBuilder(this)
            .setTitle(student.fullName)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openDetails(student)
                    1 -> openForm(student)
                    else -> confirmDelete(student)
                }
            }
            .show()
    }

    private fun confirmDelete(student: Student) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_title)
            .setMessage(getString(R.string.delete_message, student.fullName))
            .setPositiveButton(R.string.remove) { _, _ ->
                if (StudentRepository.remove(student.id)) {
                    toast(getString(R.string.deleted_success))
                    refresh()
                } else {
                    toast(getString(R.string.student_not_found))
                    finish()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
