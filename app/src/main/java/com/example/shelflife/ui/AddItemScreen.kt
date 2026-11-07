package com.example.shelflife.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.shelflife.data.FoodItem
import com.example.shelflife.data.ItemStatus
import com.example.shelflife.data.StorageLocation
import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import java.util.concurrent.TimeUnit

// Add Item form. Passing an itemId opens the same form filled in with that item,
// and saving updates it instead of adding a new one.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    viewModel: FoodViewModel,
    itemId: Long?,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    onUseSome: ((FoodItem, Double) -> Unit)? = null
) {
    val isEdit = itemId != null

    var name by rememberSaveable { mutableStateOf("") }
    var quantity by rememberSaveable { mutableStateOf("1") }
    var location by rememberSaveable { mutableStateOf(StorageLocation.FRIDGE) }
    var expiryMillis by rememberSaveable { mutableStateOf(startOfToday() + TimeUnit.DAYS.toMillis(7)) }
    var notes by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }

    // The item as it is in the database. Kept so fields the form doesn't show survive the edit.
    var original by remember { mutableStateOf<FoodItem?>(null) }
    // Only fill the fields once, so a rotation doesn't wipe what the user has typed.
    var filled by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(itemId) {
        if (itemId == null) return@LaunchedEffect
        val item = viewModel.getItem(itemId) ?: return@LaunchedEffect
        original = item
        if (!filled) {
            name = item.name
            quantity = formatQuantity(item.quantity)
            location = item.location
            expiryMillis = item.expiryDate.time
            notes = item.notes.orEmpty()
            price = item.price?.let { formatQuantity(it) }.orEmpty()
            filled = true
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showUseSome by remember { mutableStateOf(false) }

    val parsedQuantity = quantity.toDoubleOrNull()
    val parsedPrice = price.toDoubleOrNull()
    val canSave = name.isNotBlank() &&
        parsedQuantity != null && parsedQuantity > 0 &&
        (price.isBlank() || (parsedPrice != null && parsedPrice >= 0)) &&
        (!isEdit || original != null)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it },
            label = { Text("Quantity") },
            singleLine = true,
            isError = quantity.isNotEmpty() && (parsedQuantity == null || parsedQuantity <= 0),
            supportingText = original?.let { item ->
                if (item.quantityRemaining != item.quantity) {
                    { Text("${formatQuantity(item.quantityRemaining)} left") }
                } else null
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Text("Stored in", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StorageLocation.entries.forEach { option ->
                FilterChip(
                    selected = location == option,
                    onClick = { location = option },
                    label = { Text(option.label()) }
                )
            }
        }

        OutlinedTextField(
            value = DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(expiryMillis)),
            onValueChange = {},
            readOnly = true,
            label = { Text("Expiry date") },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Filled.DateRange, contentDescription = "Pick expiry date")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price (optional)") },
            singleLine = true,
            isError = price.isNotEmpty() && (parsedPrice == null || parsedPrice < 0),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val qty = parsedQuantity ?: return@Button
                val base = original
                val item = if (base == null) {
                    FoodItem(
                        name = name.trim(),
                        quantity = qty,
                        location = location,
                        expiryDate = Date(expiryMillis),
                        notes = notes.trim().ifEmpty { null },
                        price = parsedPrice
                    )
                } else {
                    // If the quantity changed, shift what's left by the same amount.
                    val remaining = (base.quantityRemaining + (qty - base.quantity)).coerceIn(0.0, qty)
                    base.copy(
                        name = name.trim(),
                        quantity = qty,
                        quantityRemaining = remaining,
                        location = location,
                        expiryDate = Date(expiryMillis),
                        notes = notes.trim().ifEmpty { null },
                        price = parsedPrice
                    )
                }
                viewModel.save(item)
                onDone()
            },
            enabled = canSave,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isEdit) "Save changes" else "Add item")
        }

        val current = original
        if (onUseSome != null && current != null && current.status == ItemStatus.ACTIVE) {
            OutlinedButton(onClick = { showUseSome = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Use some")
            }
        }
    }

    val current = original
    if (showUseSome && current != null && onUseSome != null) {
        UseSomeDialog(
            item = current,
            onConfirm = { amount ->
                showUseSome = false
                onUseSome(current, amount)
                onDone()
            },
            onDismiss = { showUseSome = false }
        )
    }

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = localToUtcDate(expiryMillis))
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { expiryMillis = utcToLocalDate(it) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

private fun startOfToday(): Long = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
}.timeInMillis

// The date picker works in UTC midnights; items store local midnights.
private fun utcToLocalDate(utcMillis: Long): Long {
    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = utcMillis }
    return Calendar.getInstance().apply {
        clear()
        set(utc.get(Calendar.YEAR), utc.get(Calendar.MONTH), utc.get(Calendar.DAY_OF_MONTH))
    }.timeInMillis
}

private fun localToUtcDate(localMillis: Long): Long {
    val local = Calendar.getInstance().apply { timeInMillis = localMillis }
    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH))
    }.timeInMillis
}

// Asks how much of an item was used. Anything up to what's left is allowed;
// using all of it marks the item Used.
@Composable
private fun UseSomeDialog(
    item: FoodItem,
    onConfirm: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    var amount by rememberSaveable { mutableStateOf("") }
    val parsed = amount.toDoubleOrNull()
    val valid = parsed != null && parsed > 0 && parsed <= item.quantityRemaining

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Use some ${item.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("${formatQuantity(item.quantityRemaining)} left")
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount used") },
                    singleLine = true,
                    isError = amount.isNotEmpty() && !valid,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                TextButton(onClick = { onConfirm(item.quantityRemaining) }) {
                    Text("Used it all")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { parsed?.let(onConfirm) }, enabled = valid) { Text("Use") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
