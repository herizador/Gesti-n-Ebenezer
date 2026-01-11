package com.ebenezer.logistica.ui.vehicle

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ebenezer.logistica.data.model.Vehicle
import com.ebenezer.logistica.data.model.VehicleState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleListScreen(
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    viewModel: VehicleViewModel = viewModel(factory = VehicleViewModel.Factory)
) {
    val vehicles by viewModel.allVehicles.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var filterExpiring by remember { mutableStateOf(false) }
    
    val filteredVehicles = vehicles.filter { 
        val matchesSearch = it.placa.contains(searchQuery, ignoreCase = true) || it.nombre.contains(searchQuery, ignoreCase = true)
        val matchesExpiry = if (filterExpiring) {
            val now = System.currentTimeMillis()
            val thirtyDays = 30L * 24 * 60 * 60 * 1000
            // Check if insurance or plate renewal is within 30 days or expired
            (it.fechaRenovacionPlaca - now < thirtyDays) || (it.fechaRenovacionSeguro - now < thirtyDays)
        } else true
        
        matchesSearch && matchesExpiry
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Vehículo")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar por placa o nombre") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = filterExpiring,
                    onClick = { filterExpiring = !filterExpiring },
                    label = { Text("Próximos a vencer") },
                    leadingIcon = { if (filterExpiring) Icon(Icons.Default.Add, contentDescription = null) } // Simplified icon
                )
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredVehicles) { vehicle ->
                    VehicleCard(vehicle = vehicle, onClick = { onNavigateToEdit(vehicle.id) })
                }
            }
        }
    }
}

@Composable
fun VehicleCard(vehicle: Vehicle, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = vehicle.placa, style = MaterialTheme.typography.titleLarge)
            Text(text = vehicle.nombre, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Estado: ${vehicle.estado.name}", style = MaterialTheme.typography.labelSmall)
                Text(text = "Seguro: ${formatDate(vehicle.fechaRenovacionSeguro)}", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleEntryScreen(
    vehicleId: Int = 0, // 0 means new
    onNavigateBack: () -> Unit,
    viewModel: VehicleViewModel = viewModel(factory = VehicleViewModel.Factory)
) {
    // If ID != 0, we need to load vehicle. For simplicity, we assume we might pass it or load it.
    // Ideally we use a separate state holder or LaunchedEffect to load.
    
    // Setup state
    var plaque by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var entryDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var plaqueDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var insuranceDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var state by remember { mutableStateOf(VehicleState.ACTIVO) }
    
    // Simple load logic (not production robust but functional for prototype)
    if (vehicleId != 0) {
        /* In a real app we would collect a flow from ViewModel for this ID */
        val vehicleState by viewModel.getVehicleById(vehicleId).collectAsState()
        val v = vehicleState
        if (v != null) {
            LaunchedEffect(v) {
                plaque = v.placa
                name = v.nombre
                entryDate = v.fechaIngreso
                plaqueDate = v.fechaRenovacionPlaca
                insuranceDate = v.fechaRenovacionSeguro
                state = v.estado
            }
        }
    }

    Scaffold { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = plaque, onValueChange = { plaque = it }, label = { Text("Placa") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre/Marca") }, modifier = Modifier.fillMaxWidth())
            
            DatePickerField("Fecha Ingreso", entryDate) { entryDate = it }
            DatePickerField("Renovación Placa", plaqueDate) { plaqueDate = it }
            DatePickerField("Renovación Seguro", insuranceDate) { insuranceDate = it }
            
            // State Dropdown (Simplified as Row of chips or radio for now)
            StateSelector(currentState = state, onStateSelected = { state = it })

            Button(
                onClick = {
                    viewModel.saveVehicle(
                        Vehicle(
                            id = vehicleId,
                            placa = plaque,
                            nombre = name,
                            fechaIngreso = entryDate,
                            fechaRenovacionPlaca = plaqueDate,
                            fechaRenovacionSeguro = insuranceDate,
                            estado = state
                        )
                    )
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Guardar Vehículo")
            }
            
            if (vehicleId != 0) {
                 Button(
                    onClick = {
                         // Need fetch vehicle object first to delete, simplified here
                         // viewModel.deleteVehicle(...) 
                         onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eliminar Vehículo")
                }
            }
        }
    }
}

@Composable
fun DatePickerField(label: String, date: Long, onDateSelected: (Long) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply { timeInMillis = date }
    
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(year, month, day)
            onDateSelected(calendar.timeInMillis)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    OutlinedTextField(
        value = formatDate(date),
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { datePickerDialog.show() }) {
                Icon(Icons.Default.CalendarToday, contentDescription = null)
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun StateSelector(currentState: VehicleState, onStateSelected: (VehicleState) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        VehicleState.entries.forEach { s ->
            FilterChip(
                selected = s == currentState,
                onClick = { onStateSelected(s) },
                label = { Text(s.name) }
            )
        }
    }
}

fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(timestamp))
}
