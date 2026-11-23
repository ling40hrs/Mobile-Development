package com.studentmanagement.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.studentmanagement.R
import com.studentmanagement.databinding.ItemEnrollmentBinding
import com.studentmanagement.model.Enrollment
import com.studentmanagement.model.Subject
import com.studentmanagement.util.toGradeText

class EnrollmentAdapter(
    private val onGradeClick: (Subject) -> Unit
) : RecyclerView.Adapter<EnrollmentAdapter.EnrollmentViewHolder>() {

    private val items = mutableListOf<Enrollment>()

    fun submit(newItems: List<Enrollment>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnrollmentViewHolder {
        val binding = ItemEnrollmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EnrollmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EnrollmentViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class EnrollmentViewHolder(
        private val binding: ItemEnrollmentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(enrollment: Enrollment) {
            val context = binding.root.context
            binding.textSubjectCode.text = enrollment.subject.code
            binding.textSubjectTitle.text = enrollment.subject.title
            binding.textSubjectGrade.text = enrollment.grade?.let { grade ->
                context.getString(R.string.grade_value, grade.toGradeText())
            } ?: context.getString(R.string.no_grade_yet)
            binding.root.setOnClickListener { onGradeClick(enrollment.subject) }
        }
    }
}
