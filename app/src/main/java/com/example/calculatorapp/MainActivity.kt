package com.example.calculatorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculatorapp.ui.theme.CalculatorAppTheme
import net.objecthunter.exp4j.ExpressionBuilder

// Custom Colors
val ColorPink = Color(0xFFF8D2D6)
val ColorLightBlue = Color(0xFFEAF3F7)
val ColorOrange = Color(0xFFD68F1F)
val ColorGreen = Color(0xFF1CB37E)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkMode by remember { mutableStateOf(false) }
            CalculatorAppTheme(darkTheme = isDarkMode) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    CalculatorScreen(
                        modifier = Modifier.padding(innerPadding),
                        isDarkMode = isDarkMode,
                        onThemeToggle = { isDarkMode = !isDarkMode }
                    )
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit
) {
    var expression by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    val buttons = listOf(
        listOf("C", "del", "%", "/"),
        listOf("7", "8", "9", "x"),
        listOf("4", "5", "6", "-"),
        listOf("1", "2", "3", "+"),
        listOf("00", "0", ".", "=")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        // Theme Toggle Button
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = onThemeToggle,
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = if (isDarkMode) "☀️" else "🌙",
                    fontSize = 24.sp
                )
            }
        }

        // Display Area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = expression,
                style = TextStyle(
                    fontSize = 24.sp,
                    color = if (isDarkMode) Color.LightGray else Color.Gray
                )
            )
            Text(
                text = if (result.isEmpty()) "0" else result,
                style = TextStyle(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }

        // Buttons Grid
        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { label ->
                    CalculatorButton(
                        label = label,
                        isDarkMode = isDarkMode,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(vertical = 5.dp)
                    ) {
                        when (label) {
                            "C" -> {
                                expression = ""
                                result = ""
                            }
                            "del" -> {
                                if (result.isNotEmpty()) {
                                    result = "" // Clear result if starting to delete from it
                                } else if (expression.isNotEmpty()) {
                                    expression = expression.dropLast(1)
                                }
                            }
                            "%" -> {
                                val target = if (result.isNotEmpty()) result else expression
                                if (target.isNotEmpty()) {
                                    try {
                                        val value = target.toDouble() / 100
                                        expression = value.toString()
                                        result = ""
                                    } catch (e: Exception) {
                                        // Handle case where target is not just a number
                                    }
                                }
                            }
                            "=" -> {
                                if (expression.isNotEmpty()) {
                                    try {
                                        val evalExpression = expression.replace("x", "*")
                                        val evalResult = ExpressionBuilder(evalExpression).build().evaluate()
                                        // Format result: remove .0 if it's a whole number
                                        result = if (evalResult % 1 == 0.0) {
                                            evalResult.toLong().toString()
                                        } else {
                                            evalResult.toString()
                                        }
                                    } catch (e: Exception) {
                                        result = "Error"
                                    }
                                }
                            }
                            else -> {
                                if (result.isNotEmpty()) {
                                    if (label in listOf("+", "-", "x", "/")) {
                                        // Continue calculation with result
                                        expression = result + label
                                    } else {
                                        // Start new calculation
                                        expression = label
                                    }
                                    result = ""
                                } else {
                                    expression += label
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    label: String,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor = if (isDarkMode) {
        when (label) {
            "C" -> Color(0xFF633D41) // Muted Dark Pink
            "/", "x", "-", "+" -> ColorOrange
            "=" -> ColorGreen
            else -> Color(0xFF2C2C2C) // Dark Gray
        }
    } else {
        when (label) {
            "C" -> ColorPink
            "/", "x", "-", "+" -> ColorOrange
            "=" -> ColorGreen
            else -> ColorLightBlue
        }
    }

    val contentColor = when (label) {
        "/", "x", "-", "+", "=" -> Color.White
        else -> if (isDarkMode) Color.White else Color.Black
    }

    Surface(
        shape = CircleShape,
        color = backgroundColor,
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = contentColor,
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
            )
        }
    }
}
