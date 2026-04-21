package com.example.musicplayerlab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.musicplayerlab.ui.screens.MusicPlayerLabApp
import com.example.musicplayerlab.ui.theme.MusicPlayerLabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicPlayerLabTheme {
                MusicPlayerLabApp()
            }
        }
    }
}

