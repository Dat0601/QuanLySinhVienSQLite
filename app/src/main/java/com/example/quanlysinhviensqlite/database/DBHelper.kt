package com.example.quanlysinhviensqlite.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.quanlysinhviensqlite.model.Student

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "student_manager.db"
        private const val DATABASE_VERSION = 1

        // Tên bảng và cột
        private const val TABLE_STUDENTS = "students"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_AGE = "age"
        private const val COLUMN_EMAIL = "email"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("CREATE TABLE $TABLE_STUDENTS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_NAME TEXT NOT NULL, "
                + "$COLUMN_AGE INTEGER NOT NULL, "
                + "$COLUMN_EMAIL TEXT NOT NULL"
                + ")")
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Nếu có nâng version, drop table cũ và tạo lại
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STUDENTS")
        onCreate(db)
    }

    /** Thêm sinh viên mới **/
    fun addStudent(student: Student): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, student.name)
            put(COLUMN_AGE, student.age)
            put(COLUMN_EMAIL, student.email)
        }
        // insert trả về rowId (hoặc -1 nếu lỗi)
        val result = db.insert(TABLE_STUDENTS, null, values)
        db.close()
        return result
    }

    /** Lấy toàn bộ danh sách sinh viên **/
    fun getAllStudents(): ArrayList<Student> {
        val studentList = ArrayList<Student>()
        val selectQuery = "SELECT * FROM $TABLE_STUDENTS"
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val student = Student(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    age = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
                )
                studentList.add(student)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return studentList
    }

    /** Cập nhật thông tin sinh viên **/
    fun updateStudent(student: Student): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, student.name)
            put(COLUMN_AGE, student.age)
            put(COLUMN_EMAIL, student.email)
        }
        // whereClause: "id = ?", whereArgs: [student.id]
        val rowsAffected = db.update(
            TABLE_STUDENTS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(student.id.toString())
        )
        db.close()
        return rowsAffected
    }

    /** Xóa sinh viên theo id **/
    fun deleteStudent(id: Int): Int {
        val db = this.writableDatabase
        val rowsDeleted = db.delete(
            TABLE_STUDENTS,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
        db.close()
        return rowsDeleted
    }
}