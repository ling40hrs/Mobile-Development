package com.studentmanagement.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.studentmanagement.R
import com.studentmanagement.data.StudentRepository
import com.studentmanagement.databinding.ActivityStudentFormBinding
import com.studentmanagement.model.Gender
import com.studentmanagement.model.Student
import com.studentmanagement.model.StudentInput
import com.studentmanagement.model.YearLevel
import com.studentmanagement.util.toast
import com.studentmanagement.util.value
import com.studentmanagement.validation.FieldError
import com.studentmanagement.validation.Validation
import com.studentmanagement.validation.Validator

class StudentFormActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_STUDENT_ID = "extra_student_id"
        private const val UNKNOWN_ID = -1
    }

    private lateinit var binding: ActivityStudentFormBinding
    private var editingId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.buttonSave.setOnClickListener { saveStudent() }
        binding.buttonCancelForm.setOnClickListener { finish() }
        editingId = intent.getIntExtra(EXTRA_STUDENT_ID, UNKNOWN_ID).takeIf { it != UNKNOWN_ID }
        editingId?.let { id ->
            val student = StudentRepository.findById(id)
            if (student == null) {
                toast(getString(R.string.student_not_found))
                finish()
                return
            }
            setupEditMode(student)
        }
    }

    private fun setupEditMode(student: Student) {
        binding.toolbar.title = getString(R.string.edit_student)
        binding.editStudentNumber.setText(student.studentNumber)
        binding.editFirstName.setText(student.firstName)
        binding.editLastName.setText(student.lastName)
        binding.editEmail.setText(student.email)
        binding.editAge.setText(student.age.toString())
        binding.editContact.setText(student.contactNumber)
        when (student.gender) {
            Gender.MALE -> binding.radioMale.isChecked = true
            Gender.FEMALE -> binding.radioFemale.isChecked = true
        }
        when (student.yearLevel) {
            YearLevel.FIRST -> binding.radioYear1.isChecked = true
            YearLevel.SECOND -> binding.radioYear2.isChecked = true
            YearLevel.THIRD -> binding.radioYear3.isChecked = true
            YearLevel.FOURTH -> binding.radioYear4.isChecked = true
        }
        binding.checkConfirm.isChecked = true
    }

    private fun saveStudent() {
        when (val result = StudentRepository.save(collectInput(), editingId)) {
            is Validation.Success -> {
                val message = if (editingId == null) {
                    getString(R.string.registered_success)
                } else {
                    getString(R.string.updated_success)
                }
                toast(message)
                finish()
            }

            is Validation.Failure -> showErrors(result.errors)
        }
    }

    private fun collectInput(): StudentInput = StudentInput(
        studentNumber = binding.editStudentNumber.value(),
        firstName = binding.editFirstName.value(),
        lastName = binding.editLastName.value(),
        email = binding.editEmail.value(),
        age = binding.editAge.value(),
        contactNumber = binding.editContact.value(),
        gender = selectedGender(),
        yearLevel = selectedYearLevel(),
        confirmed = binding.checkConfirm.isChecked
    )

    private fun selectedGender(): Gender? = when (binding.radioGroupGender.checkedRadioButtonId) {
        R.id.radioMale -> Gender.MALE
        R.id.radioFemale -> Gender.FEMALE
        else -> null
    }

    private fun selectedYearLevel(): YearLevel? = when (binding.radioGroupYear.checkedRadioButtonId) {
        R.id.radioYear1 -> YearLevel.FIRST
        R.id.radioYear2 -> YearLevel.SECOND
        R.id.radioYear3 -> YearLevel.THIRD
        R.id.radioYear4 -> YearLevel.FOURTH
        else -> null
    }

    private fun showErrors(errors: List<FieldError>) {
        binding.inputLayoutStudentNumber.error = textFor(errors, Validator.FIELD_STUDENT_NUMBER)
        binding.inputLayoutFirstName.error = textFor(errors, Validator.FIELD_FIRST_NAME)
        binding.inputLayoutLastName.error = textFor(errors, Validator.FIELD_LAST_NAME)
        binding.inputLayoutEmail.error = textFor(errors, Validator.FIELD_EMAIL)
        binding.inputLayoutAge.error = textFor(errors, Validator.FIELD_AGE)
        binding.inputLayoutContact.error = textFor(errors, Validator.FIELD_CONTACT)
        showInlineError(binding.textGenderError, textFor(errors, Validator.FIELD_GENDER))
        showInlineError(binding.textYearError, textFor(errors, Validator.FIELD_YEAR_LEVEL))
        showInlineError(binding.textConfirmError, textFor(errors, Validator.FIELD_CONFIRMATION))
        val banner = errors.firstOrNull()?.let { getString(it.messageRes) }
        binding.textFormError.text = banner
        binding.textFormError.isVisible = banner != null
        binding.scrollViewForm.post { binding.scrollViewForm.smoothScrollTo(0, 0) }
    }

    private fun textFor(errors: List<FieldError>, field: String): String? =
        errors.firstOrNull { it.field == field }?.let { getString(it.messageRes) }

    private fun showInlineError(view: android.widget.TextView, message: String?) {
        view.text = message
        view.isVisible = message != null
    }
}
