package com.tradernishu.aviatoranalyzer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

private val Bg = Color(0xFF090D12)
private val Panel = Color(0xFF151D25)
private val Green = Color(0xFF35E58A)
private val Muted = Color(0xFF9EABB8)
private val Red = Color(0xFFFF6675)

data class Round(val multiplier: Double)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AnalyzerApp() }
    }
}

@Composable
fun AnalyzerApp() {
    var entry by remember { mutableStateOf("") }
    var rounds by remember { mutableStateOf(listOf<Round>()) }
    var error by remember { mutableStateOf("") }

    val low = rounds.count { it.multiplier < 2.0 }
    val mid = rounds.count { it.multiplier >= 2.0 && it.multiplier < 5.0 }
    val high = rounds.count { it.multiplier >= 5.0 }
    val total = rounds.size.coerceAtLeast(1)
    val average = if (rounds.isNotEmpty()) rounds.map { it.multiplier }.average() else 0.0

    MaterialTheme(colorScheme = darkColorScheme(
        background = Bg, surface = Panel, primary = Green,
        onBackground = Color.White, onSurface = Color.White
    )) {
        Column(
            modifier = Modifier.fillMaxSize().background(Bg).padding(18.dp)
        ) {
            Text("55 CLUB", color = Green, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text("AVIATOR ANALYZER", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            Text("Manual history • Statistical analysis", color = Muted, fontSize = 13.sp)
            Spacer(Modifier.height(18.dp))

            Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("ADD ROUND RESULT", color = Green, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = entry,
                            onValueChange = { entry = it; error = "" },
                            modifier = Modifier.weight(1f),
                            label = { Text("Multiplier (e.g. 2.45x)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = {
                            val value = entry.trim().lowercase(Locale.US).removeSuffix("x").toDoubleOrNull()
                            if (value == null || value < 1.0 || value > 10000.0) {
                                error = "Enter a valid multiplier from 1.00 to 10000."
                            } else {
                                rounds = listOf(Round(value)) + rounds
                                entry = ""
                            }
                        }) { Text("ADD") }
                    }
                    if (error.isNotEmpty()) Text(error, color = Red, fontSize = 12.sp)
                    Text("Enter results manually from your game history. No account login is needed.", color = Muted, fontSize = 11.sp)
                }
            }

            Spacer(Modifier.height(14.dp))
            Text("HISTORY STATISTICS", color = Green, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("TOTAL", rounds.size.toString(), Modifier.weight(1f))
                StatCard("AVERAGE", if (rounds.isEmpty()) "—" else String.format(Locale.US, "%.2fx", average), Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("< 2x", "$low (${pct(low, total)}%)", Modifier.weight(1f), Red)
                StatCard("2x–5x", "$mid (${pct(mid, total)}%)", Modifier.weight(1f), Color(0xFFFFC857))
                StatCard("5x+", "$high (${pct(high, total)}%)", Modifier.weight(1f), Green)
            }

            Spacer(Modifier.height(14.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(14.dp)) {
                    Text("AI-STYLE INSIGHT", color = Green, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(Modifier.height(6.dp))
                    val insight = when {
                        rounds.size < 10 -> "Add at least 10 past results for a more useful summary. This tool describes history only."
                        else -> "In your ${rounds.size} entered rounds: ${pct(low, total)}% were below 2x, ${pct(mid, total)}% were 2x–5x, and ${pct(high, total)}% were 5x+. These frequencies do not predict the next round."
                    }
                    Text(insight, color = Color.White, fontSize = 13.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("No guaranteed predictions. Each round may be independent; past results cannot reveal the next multiplier.", color = Muted, fontSize = 11.sp)
                }
            }

            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("RECENT ROUNDS", color = Green, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { rounds = emptyList() }) { Text("CLEAR ALL", color = Red) }
            }
            if (rounds.isEmpty()) {
                Text("No rounds added yet.", color = Muted, fontSize = 13.sp)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(rounds.take(100).withIndex().toList()) { (index, round) ->
                        Row(
                            Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(10.dp)).padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("#${index + 1}", color = Muted, modifier = Modifier.weight(1f))
                            Text(String.format(Locale.US, "%.2fx", round.multiplier),
                                color = if (round.multiplier >= 5) Green else if (round.multiplier < 2) Red else Color(0xFFFFC857),
                                fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier, valueColor: Color = Color.White) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(label, color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(5.dp))
            Text(value, color = valueColor, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

fun pct(count: Int, total: Int): Int = if (total == 0) 0 else (count * 100.0 / total).toInt()
