package be.he2b.scoutpocket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import be.he2b.scoutpocket.ui.LoginScreen
import be.he2b.scoutpocket.ui.theme.ScoutPocketTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScoutPocketTheme {
                LoginScreen()
            }
        }
    }
}
