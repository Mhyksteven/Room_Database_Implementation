
package com.example.room_database_discussion.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.room_database_discussion.data.Student
import com.example.room_database_discussion.data.StudentDatabase
import com.example.room_database_discussion.data.StudentRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudentViewModel(application: Application) :
    AndroidViewModel(application) {

    private val database = StudentDatabase.getData(application)

    private val repository =
        StudentRepository(database.studentDao())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> =
        _searchQuery.asStateFlow()

    private val _courseFilter = MutableStateFlow("")
    val courseFilter: StateFlow<String> =
        _courseFilter.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val students: StateFlow<List<Student>> =
        combine(_searchQuery, _courseFilter) { query, course ->
            query to course
        }
            .flatMapLatest { (query, course) ->
                repository.filterStudents(query, course)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val totalStudentCount: StateFlow<Int> =
        repository.getTotalStudentCount()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 0
            )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateCourseFilter(course: String) {
        _courseFilter.value = course
    }

    fun addStudent(
        name: String,
        email: String,
        course: String,
        age: Int
    ) {
        viewModelScope.launch {
            repository.insertStudent(
                Student(
                    name = name.trim(),
                    email = email.trim(),
                    course = course.trim(),
                    age = age
                )
            )
        }
    }

    fun updateStudent(student: Student) {
        viewModelScope.launch {
            repository.updateStudent(student)
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            repository.deleteStudent(student)
        }
    }
}
