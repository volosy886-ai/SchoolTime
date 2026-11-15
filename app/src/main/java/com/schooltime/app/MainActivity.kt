package com.schooltime.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SchoolTimeScreen() }
    }
}

@Composable
fun SchoolTimeScreen() {
    val background = Color(0xFF090D14)
    val card = Color(0xFF171F2B)
    val blue = Color(0xFF398BFF)
    val green = Color(0xFF55E69A)

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = background) {
            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "SCHOOLTIME",
                    color = blue,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp
                )
                Spacer(Modifier.height(24.dp))
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .background(card, RoundedCornerShape(28.dp))
                        .padding(24.dp)
                ) {
                    Text("ПОНЕДЕЛЬНИК · 3 УРОК", color = Color.LightGray, fontSize = 13.sp)
                    Spacer(Modifier.height(24.dp))
                    Text("Русский язык", color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(28.dp))
                    Text("ДО КОНЦА УРОКА", color = Color.Gray, fontSize = 12.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("25:00", color = green, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(28.dp))
                    Box(Modifier.fillMaxWidth().height(1.dp).background(Color.DarkGray))
                    Spacer(Modifier.height(18.dp))
                    Text("СЛЕДУЮЩИЙ УРОК", color = Color.Gray, fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Литература", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(20.dp))
                Text("Твоё расписание всегда под рукой 💙", color = Color.Gray, fontSize = 13.sp)
            }
        }
    }
}
