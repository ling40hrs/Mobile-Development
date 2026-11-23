package com.studentmanagement.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.studentmanagement.R
import com.studentmanagement.data.StudentRepository
import com.studentmanagement.databinding.ActivityMainBinding
import com.studentmanagement.util.toGradeText

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StudentRepository.seedIfEmpty()
        binding.buttonRegister.setOnClickListener { openForm() }
        binding.buttonViewStudents.setOnClickListener { openList(focusSearch = false) }
        binding.buttonSearchStudents.setOnClickListener { openList(focusSearch = true) }
        binding.cardHowToUse.setOnClickListener { showHowToUse() }
    }

    override fun onResume() {
        super.onResume()
        renderStats()
    }

    private fun openForm() {
        startActivity(Intent(this, StudentFormActivity::class.java))
    }

    private fun openList(focusSearch: Boolean) {
        val intent = Intent(this, StudentListActivity::class.java)
        intent.putExtra(StudentListActivity.EXTRA_FOCUS_SEARCH, focusSearch)
        startActivity(intent)
    }

    private fun showHowToUse() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.how_to_use)
            .setMessage(R.string.how_to_use_message)
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    private fun renderStats() {
        binding.textStatStudents.text = StudentRepository.size.toString()
        binding.textStatEnrollments.text = StudentRepository.totalEnrollments().toString()
        binding.textStatAverage.text = StudentRepository.overallAverage()?.toGradeText()
            ?: getString(R.string.value_not_available)
    }
}
