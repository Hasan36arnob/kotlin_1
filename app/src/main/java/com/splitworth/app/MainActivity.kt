package com.splitworth.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.splitworth.app.core.SplitCalculator
import com.splitworth.app.core.SplitInput

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SplitWorthScreen()
            }
        }
    }
}

@Composable
private fun SplitWorthScreen() {
    var subtotalInput by rememberSaveable { mutableStateOf("") }
    var taxInput by rememberSaveable { mutableStateOf("8.25") }
    var tipInput by rememberSaveable { mutableStateOf("15") }
    var peopleInput by rememberSaveable { mutableStateOf("2") }
    var roundUpPerPerson by rememberSaveable { mutableStateOf(false) }
    var resultText by rememberSaveable { mutableStateOf("Enter values and tap Calculate") }
    var detailsText by rememberSaveable { mutableStateOf("") }
    val history = remember { mutableStateListOf<String>() }
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        Text("SplitWorth", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Bill split calculator for teams and friend groups.")

        Spacer(modifier = Modifier.height(16.dp))

        NumericField(
            value = subtotalInput,
            onValueChange = { subtotalInput = it },
            label = "Subtotal (USD)"
        )

        Spacer(modifier = Modifier.height(8.dp))

        NumericField(
            value = taxInput,
            onValueChange = { taxInput = it },
            label = "Tax %"
        )

        Spacer(modifier = Modifier.height(8.dp))

        NumericField(
            value = tipInput,
            onValueChange = { tipInput = it },
            label = "Tip %"
        )

        Spacer(modifier = Modifier.height(8.dp))

        NumericField(
            value = peopleInput,
            onValueChange = { peopleInput = it },
            label = "People",
            integerOnly = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = roundUpPerPerson,
                onCheckedChange = { roundUpPerPerson = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Round up each person to nearest cent")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                val subtotal = subtotalInput.toDoubleOrNull()
                val taxPercent = taxInput.toDoubleOrNull()
                val tipPercent = tipInput.toDoubleOrNull()
                val people = peopleInput.toIntOrNull()

                if (subtotal == null || taxPercent == null || tipPercent == null || people == null) {
                    resultText = "Please enter valid numbers."
                    detailsText = ""
                    return@Button
                }

                val input = SplitInput(
                    subtotal = subtotal,
                    taxPercent = taxPercent,
                    tipPercent = tipPercent,
                    people = people,
                    roundUpPerPerson = roundUpPerPerson
                )

                val output = SplitCalculator.calculate(input)
                if (output.isFailure) {
                    resultText = output.exceptionOrNull()?.message ?: "Invalid input."
                    detailsText = ""
                    return@Button
                }

                val result = output.getOrThrow()
                val perPerson = SplitCalculator.formatCurrency(result.perPerson)
                val total = SplitCalculator.formatCurrency(result.total)
                val taxAmount = SplitCalculator.formatCurrency(result.taxAmount)
                val tipAmount = SplitCalculator.formatCurrency(result.tipAmount)

                resultText = "Each person pays $$perPerson"
                detailsText = "Total $$total | Tax $$taxAmount | Tip $$tipAmount"

                val historyLine = "$$total total -> $$perPerson each for $people"
                if (history.size == 8) history.removeAt(history.lastIndex)
                history.add(0, historyLine)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val textToCopy = if (detailsText.isBlank()) resultText else "$resultText | $detailsText"
                clipboardManager.setText(AnnotatedString(textToCopy))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Copy result")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Result", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(resultText)
                if (detailsText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(detailsText)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("Recent", fontWeight = FontWeight.SemiBold)
        history.forEach { item ->
            Text("- $item")
        }
    }
}

@Composable
private fun NumericField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    integerOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            val filtered = if (integerOnly) {
                it.filter { ch -> ch.isDigit() }
            } else {
                it.filter { ch -> ch.isDigit() || ch == '.' }
            }
            onValueChange(filtered)
        },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (integerOnly) KeyboardType.Number else KeyboardType.Decimal
        )
    )
}
