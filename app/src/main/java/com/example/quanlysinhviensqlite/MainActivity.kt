package com.example.quanlysinhviensqlite

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlysinhviensqlite.adapter.StudentAdapter
import com.example.quanlysinhviensqlite.database.DBHelper
import com.example.quanlysinhviensqlite.model.Student

class MainActivity : AppCompatActivity(), StudentAdapter.OnItemClickListener {

    // View
    private lateinit var etName: EditText
    private lateinit var etAge: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnAddUpdate: Button
    private lateinit var rvStudents: RecyclerView

    // Database helper
    private lateinit var dbHelper: DBHelper

    // Adapter & data
    private lateinit var studentAdapter: StudentAdapter
    private var studentList = ArrayList<Student>()

    // Biến lưu trạng thái: null nếu thêm mới, khác null nếu đang sửa
    private var selectedStudent: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Liên kết view
        etName = findViewById(R.id.etName)
        etAge = findViewById(R.id.etAge)
        etEmail = findViewById(R.id.etEmail)
        btnAddUpdate = findViewById(R.id.btnAddUpdate)
        rvStudents = findViewById(R.id.rvStudents)

        // 2. Khởi tạo DBHelper
        dbHelper = DBHelper(this)

        // 3. Thiết lập RecyclerView
        rvStudents.layoutManager = LinearLayoutManager(this)
        studentAdapter = StudentAdapter(studentList, this)
        rvStudents.adapter = studentAdapter

        // 4. Tải dữ liệu ban đầu
        loadStudents()

        // 5. Bắt sự kiện cho nút Thêm/Cập nhật
        btnAddUpdate.setOnClickListener {
            val name = etName.text.toString().trim()
            val ageText = etAge.text.toString().trim()
            val email = etEmail.text.toString().trim()

            // Kiểm tra dữ liệu đầu vào
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(ageText) || TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val age = ageText.toIntOrNull()
            if (age == null) {
                Toast.makeText(this, "Tuổi phải là số nguyên", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedStudent == null) {
                // --- Chế độ Thêm mới ---
                val newStudent = Student(name = name, age = age, email = email)
                val id = dbHelper.addStudent(newStudent)
                if (id > 0) {
                    Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show()
                    clearForm()
                    loadStudents()
                } else {
                    Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show()
                }
            } else {
                // --- Chế độ Sửa ---
                val stu = selectedStudent!!
                stu.name = name
                stu.age = age
                stu.email = email
                val rows = dbHelper.updateStudent(stu)
                if (rows > 0) {
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                    clearForm()
                    loadStudents()
                } else {
                    Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
                }
                // Sau khi sửa xong, chuyển về chế độ Thêm mới
                selectedStudent = null
                btnAddUpdate.text = "Thêm"
            }
        }
    }

    /** Tải toàn bộ sinh viên từ DB và cập nhật RecyclerView **/
    private fun loadStudents() {
        studentList.clear()
        studentList.addAll(dbHelper.getAllStudents())
        studentAdapter.setData(studentList)
    }

    /** Xóa trắng form sau khi thêm/sửa thành công **/
    private fun clearForm() {
        etName.setText("")
        etAge.setText("")
        etEmail.setText("")
    }

    /** Khi người dùng bấm nút Sửa ở từng item **/
    override fun onEditClicked(student: Student) {
        // Đưa dữ liệu lên form
        selectedStudent = student
        etName.setText(student.name)
        etAge.setText(student.age.toString())
        etEmail.setText(student.email)

        // Chuyển nút thành "Cập nhật"
        btnAddUpdate.text = "Cập nhật"
    }

    /** Khi người dùng bấm nút Xóa ở từng item **/
    override fun onDeleteClicked(student: Student) {
        val rows = dbHelper.deleteStudent(student.id)
        if (rows > 0) {
            Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show()
            // Nếu đang sửa chính sinh viên này thì reset form
            if (selectedStudent?.id == student.id) {
                clearForm()
                selectedStudent = null
                btnAddUpdate.text = "Thêm"
            }
            loadStudents()
        } else {
            Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show()
        }
    }
}