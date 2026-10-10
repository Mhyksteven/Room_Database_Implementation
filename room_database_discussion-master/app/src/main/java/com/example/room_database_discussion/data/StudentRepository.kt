
package com.example.room_database_discussion.data

import kotlinx.coroutines.flow.Flow

class StudentRepository(
    private val studentDao: StudentDao
) {

    val students: Flow<List<Student>>
        get() = studentDao.getAllStudents()

    fun filterStudents(
        searchQuery: String,
        courseFilter: String
    ): Flow<List<Student>> {
        return studentDao.filterStudents(
            searchQuery,
            courseFilter
        )
    }

    fun getTotalStudentCount(): Flow<Int> {
        return studentDao.getTotalStudentCount()
    }

    suspend fun insertStudent(student: Student) {
        studentDao.insertStudent(student)
    }

    suspend fun updateStudent(student: Student) {
        studentDao.updateStudent(student)
    }

    suspend fun deleteStudent(student: Student) {
        studentDao.deleteStudent(student)
    }
}
