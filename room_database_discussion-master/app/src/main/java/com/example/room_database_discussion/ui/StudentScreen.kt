
package com.example.room_database_discussion.ui

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.room_database_discussion.data.Student
import com.example.room_database_discussion.viewmodel.StudentViewModel

private val AppPurple = Color(0xFF6750A4)
private val LightPurple = Color(0xFFF0EBFA)
private val PageBackground = Color(0xFFF7F5FC)
private val DarkText = Color(0xFF242033)
private val MutedText = Color(0xFF777386)
private val SuccessGreen = Color(0xFF247A52)
private val DangerRed = Color(0xFFBA1A1A)

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

    fun resetForm() {
        name = ""
        email = ""
        course = "BSCpE"
        ageText = ""
        editingStudent = null
        clearErrors()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = PageBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            // Dashboard header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .background(AppPurple, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SO",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Student Operations",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                    Text(
                        text = "Student management dashboard",
                        fontSize = 13.sp,
                        color = MutedText
                    )
                }
            }

            // Statistics cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Total Students",
                    value = totalStudentCount.toString(),
                    subtitle = "All records",
                    modifier = Modifier.weight(1f),
                    accent = AppPurple
                )

                StatCard(
                    title = "Matching",
                    value = students.size.toString(),
                    subtitle = "Current results",
                    modifier = Modifier.weight(1f),
                    accent = SuccessGreen
                )
            }

            // Search and filter section
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeading(
                    title = "Find Students",
                    subtitle = "Search by name or narrow results by course."
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = viewModel::updateSearchQuery,
                            label = { Text("Search by student name") },
                            placeholder = { Text("e.g. Juan Dela Cruz") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = courseFilter,
                            onValueChange = viewModel::updateCourseFilter,
                            label = { Text("Course filter") },
                            placeholder = { Text("e.g. BSCpE or BSIT") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true
                        )

                        TextButton(
                            onClick = {
                                viewModel.updateSearchQuery("")
                                viewModel.updateCourseFilter("")
                            }
                        ) {
                            Text("Clear Search and Filters")
                        }
                    }
                }
            }

            // Student form
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeading(
                    title = if (editingStudent == null) {
                        "Register Student"
                    } else {
                        "Edit Student"
                    },
                    subtitle = if (editingStudent == null) {
                        "Enter the student's information below."
                    } else {
                        "Update the selected student's information."
                    }
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                if (it.isNotBlank()) nameError = ""
                            },
                            label = { Text("Full name") },
                            placeholder = { Text("Enter full name") },
                            isError = nameError.isNotEmpty(),
                            supportingText = {
                                if (nameError.isNotEmpty()) Text(nameError)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                if (
                                    Patterns.EMAIL_ADDRESS
                                        .matcher(it.trim())
                                        .matches()
                                ) {
                                    emailError = ""
                                }
                            },
                            label = { Text("Email address") },
                            placeholder = { Text("student@example.com") },
                            isError = emailError.isNotEmpty(),
                            supportingText = {
                                if (emailError.isNotEmpty()) Text(emailError)
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = course,
                            onValueChange = {
                                course = it
                                if (it.isNotBlank()) courseError = ""
                            },
                            label = { Text("Course") },
                            placeholder = { Text("e.g. BSCpE") },
                            isError = courseError.isNotEmpty(),
                            supportingText = {
                                if (courseError.isNotEmpty()) Text(courseError)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = ageText,
                            onValueChange = {
                                ageText = it
                                if (
                                    it.toIntOrNull()?.let { age ->
                                        age in 1..120
                                    } == true
                                ) {
                                    ageError = ""
                                }
                            },
                            label = { Text("Age") },
                            placeholder = { Text("Enter age") },
                            isError = ageError.isNotEmpty(),
                            supportingText = {
                                if (ageError.isNotEmpty()) Text(ageError)
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
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

                                    resetForm()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppPurple
                            )
                        ) {
                            Text(
                                text = if (editingStudent == null) {
                                    "＋  Add Student"
                                } else {
                                    "Save Changes"
                                },
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        }

                        if (editingStudent != null) {
                            TextButton(
                                onClick = { resetForm() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Cancel Editing")
                            }
                        }
                    }
                }
            }

            // Student records
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeading(
                    title = "Student Directory",
                    subtitle = "${students.size} student(s) displayed"
                )

                if (students.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .background(
                                        LightPurple,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "—",
                                    fontSize = 28.sp,
                                    color = AppPurple
                                )
                            }

                            Text(
                                text = "No students found",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = DarkText
                            )

                            Text(
                                text = if (
                                    searchQuery.isNotBlank() ||
                                    courseFilter.isNotBlank()
                                ) {
                                    "Try changing or clearing your filters."
                                } else {
                                    "Register your first student using the form above."
                                },
                                color = MutedText,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    students.forEach { student ->
                        StudentCard(
                            student = student,
                            onEdit = {
                                editingStudent = student
                                name = student.name
                                email = student.email
                                course = student.course
                                ageText = student.age.toString()
                                clearErrors()
                            },
                            onDelete = {
                                viewModel.deleteStudent(student)

                                if (editingStudent?.id == student.id) {
                                    resetForm()
                                }
                            }
                        )
                    }
                }
            }

            Text(
                text = "Student Operations • Room Database",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = MutedText,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    accent: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(accent, CircleShape)
            )

            Text(
                text = title,
                fontSize = 12.sp,
                color = MutedText
            )

            Text(
                text = value,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MutedText
            )
        }
    }
}

@Composable
private fun SectionHeading(
    title: String,
    subtitle: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            text = title,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText
        )

        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = MutedText
        )
    }
}

@Composable
private fun StudentCard(
    student: Student,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            LightPurple,
                            RoundedCornerShape(15.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val initials = student.name
                        .trim()
                        .split(Regex("\\s+"))
                        .filter { it.isNotEmpty() }
                        .take(2)
                        .mapNotNull { it.firstOrNull() }
                        .joinToString("")
                        .uppercase()

                    Text(
                        text = initials.ifBlank { "S" },
                        color = AppPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )

                    Text(
                        text = student.email,
                        fontSize = 12.sp,
                        color = MutedText
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = {},
                    label = { Text(student.course) }
                )

                Surface(
                    shape = RoundedCornerShape(50),
                    color = LightPurple
                ) {
                    Text(
                        text = "Age ${student.age}",
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        ),
                        color = AppPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppPurple
                    )
                ) {
                    Text("Edit")
                }

                Button(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFE8E7),
                        contentColor = DangerRed
                    )
                ) {
                    Text("Delete")
                }
            }
        }
    }
}
