
package com.example.room_database_discussion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.room_database_discussion.ui.StudentScreen
import com.example.room_database_discussion.viewmodel.StudentViewModel

class MainActivity : ComponentActivity() {

    private val studentViewModel: StudentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface {
                    StudentScreen(viewModel = studentViewModel)
                }
            }
        }
    }
}
