package com.example.quanlysinhviensqlite.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlysinhviensqlite.R
import com.example.quanlysinhviensqlite.model.Student

class StudentAdapter(
    private var studentList: ArrayList<Student>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    interface OnItemClickListener {
        fun onEditClicked(student: Student)
        fun onDeleteClicked(student: Student)
    }

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNameAge: TextView = itemView.findViewById(R.id.tvNameAge)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = studentList[position]
        holder.tvNameAge.text = "${student.name} - ${student.age} tuổi"
        holder.tvEmail.text = student.email

        // Bắt sự kiện Sửa
        holder.btnEdit.setOnClickListener {
            listener.onEditClicked(student)
        }
        // Bắt sự kiện Xóa
        holder.btnDelete.setOnClickListener {
            listener.onDeleteClicked(student)
        }
    }

    override fun getItemCount(): Int = studentList.size

    /** Cập nhật dữ liệu và refresh danh sách **/
    fun setData(newList: ArrayList<Student>) {
        studentList = newList
        notifyDataSetChanged()
    }
}