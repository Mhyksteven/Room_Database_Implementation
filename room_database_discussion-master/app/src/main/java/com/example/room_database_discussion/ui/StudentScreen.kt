
package com.example.room_database_discussion.ui

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.room_database_discussion.data.Student
import com.example.room_database_discussion.viewmodel.StudentViewModel

@Composable
fun StudentScreen(viewModel: StudentViewModel) {
    val students by viewModel.students.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val courseFilter by viewModel.courseFilter.collectAsState()
    val totalStudentCount by viewModel.totalStudentCount.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("BSCpE") }
    var ageText by remember { mutableStateOf("") }
    var editingStudent by remember { mutableStateOf<Student?>(null) }

    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var courseError by remember { mutableStateOf("") }
    var ageError by remember { mutableStateOf("") }

    fun clearErrors() {
        nameError = ""
        emailError = ""
        courseError = ""
        ageError = ""
    }

    fun validateForm(): Boolean {
        nameError = if (name.isBlank()) {
            "Student name is required."
        } else {
            ""
        }

        emailError = when {
            email.isBlank() -> "Email is required."
            !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() ->
                "Enter a valid email address."
            else -> ""
        }

        courseError = if (course.isBlank()) {
            "Course is required."
        } else {
            ""
        }

        val age = ageText.toIntOrNull()
        ageError = when {
            ageText.isBlank() -> "Age is required."
            age == null -> "Age must be a whole number."
            age !in 1..120 -> "Enter an age from 1 to 120."
            else -> ""
        }

        return nameError.isEmpty() &&
                emailError.isEmpty() &&
                courseError.isEmpty() &&
                ageError.isEmpty()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Student Operations",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Total Students: $totalStudentCount",
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::updateSearchQuery,
            label = { Text("Search student by name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = courseFilter,
            onValueChange = viewModel::updateCourseFilter,
            label = { Text("Filter by course (blank = all)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        TextButton(
            onClick = { viewModel.updateCourseFilter("") }
        ) {
            Text("Show All Courses")
        }

        Text("Matching Students: ${students.size}")

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                if (it.isNotBlank()) nameError = ""
            },
            label = { Text("Student name") },
            isError = nameError.isNotEmpty(),
            supportingText = {
                if (nameError.isNotEmpty()) Text(nameError)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                if (
                    Patterns.EMAIL_ADDRESS.matcher(it.trim()).matches()
                ) {
                    emailError = ""
                }
            },
            label = { Text("Email") },
            isError = emailError.isNotEmpty(),
            supportingText = {
                if (emailError.isNotEmpty()) Text(emailError)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = course,
            onValueChange = {
                course = it
                if (it.isNotBlank()) courseError = ""
            },
            label = { Text("Course") },
            isError = courseError.isNotEmpty(),
            supportingText = {
                if (courseError.isNotEmpty()) Text(courseError)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = ageText,
            onValueChange = {
                ageText = it
                if (it.toIntOrNull() in 1..120) ageError = ""
            },
            label = { Text("Age") },
            isError = ageError.isNotEmpty(),
            supportingText = {
                if (ageError.isNotEmpty()) Text(ageError)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                if (validateForm()) {
                    val age = ageText.toInt()
                    val selected = editingStudent

                    if (selected == null) {
                        viewModel.addStudent(
                            name,
                            email,
                            course,
                            age
                        )
                    } else {
                        viewModel.updateStudent(
                            selected.copy(
                                name = name.trim(),
                                email = email.trim(),
                                course = course.trim(),
                                age = age
                            )
                        )
                    }

                    name = ""
                    email = ""
                    course = "BSCpE"
                    ageText = ""
                    editingStudent = null
                    clearErrors()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (editingStudent == null) {
                    "Add Student"
                } else {
                    "Save Changes"
                }
            )
        }

        if (editingStudent != null) {
            TextButton(
                onClick = {
                    editingStudent = null
                    name = ""
                    email = ""
                    course = "BSCpE"
                    ageText = ""
                    clearErrors()
                }
            ) {
                Text("Cancel Editing")
            }
        }

        if (students.isEmpty()) {
            Text(
                when {
                    searchQuery.isNotBlank() &&
                            courseFilter.isNotBlank() ->
                        "No students match this name and course."
                    searchQuery.isNotBlank() ->
                        "No students match \"$searchQuery\"."
                    courseFilter.isNotBlank() ->
                        "No students found for course \"$courseFilter\"."
                    else -> "No students yet."
                },
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            students.forEach { student ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = student.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text("Email: ${student.email}")
                        Text("Course: ${student.course}")
                        Text("Age: ${student.age}")

                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {
                            TextButton(
                                onClick = {
                                    editingStudent = student
                                    name = student.name
                                    email = student.email
                                    course = student.course
                                    ageText = student.age.toString()
                                    clearErrors()
                                }
                            ) {
                                Text("Edit")
                            }

                            TextButton(
                                onClick = {
                                    viewModel.deleteStudent(student)

                                    if (editingStudent?.id == student.id) {
                                        editingStudent = null
                                        name = ""
                                        email = ""
                                        course = "BSCpE"
                                        ageText = ""
                                        clearErrors()
                                    }
                                }
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}
