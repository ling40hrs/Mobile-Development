package com.studentmanagement.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.studentmanagement.R
import com.studentmanagement.databinding.ItemStudentBinding
import com.studentmanagement.model.Student
import com.studentmanagement.util.toGradeText

class StudentAdapter(
    private val onStudentClick: (Student) -> Unit,
    private val onStudentLongClick: (Student) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    private val items = mutableListOf<Student>()

    fun submit(newItems: List<Student>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class StudentViewHolder(
        private val binding: ItemStudentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(student: Student) {
            val context = binding.root.context
            binding.textAvatar.text = student.avatarInitial
            binding.textStudentName.text = student.fullName
            binding.textStudentMeta.text = context.getString(
                R.string.student_card_meta,
                student.studentNumber,
                student.yearLevel.label,
                student.gender.label
            )
            binding.textStudentSubjects.text = context.getString(
                R.string.subjects_count,
                student.enrollments.size
            )
            val average = student.averageGrade
            if (average == null) {
                binding.textAverage.text = context.getString(R.string.value_not_available)
                binding.textAverage.setBackgroundResource(R.drawable.bg_pill_muted)
                binding.textAverage.setTextColor(
                    ContextCompat.getColor(context, R.color.text_secondary)
                )
            } else {
                binding.textAverage.text = average.toGradeText()
                binding.textAverage.setBackgroundResource(R.drawable.bg_pill_success)
                binding.textAverage.setTextColor(
                    ContextCompat.getColor(context, R.color.success)
                )
            }
            binding.root.setOnClickListener { onStudentClick(student) }
            binding.root.setOnLongClickListener {
                onStudentLongClick(student)
                true
            }
        }
    }
}
