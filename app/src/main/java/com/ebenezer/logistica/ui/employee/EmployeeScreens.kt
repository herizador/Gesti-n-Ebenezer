package com.ebenezer.logistica.ui.employee

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveEta
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ebenezer.logistica.data.model.Employee
import com.ebenezer.logistica.data.model.EmployeeStatus
import com.ebenezer.logistica.ui.vehicle.DatePickerField
import com.ebenezer.logistica.ui.vehicle.VehicleViewModel
import com.ebenezer.logistica.ui.vehicle.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeListScreen(
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    viewModel: EmployeeViewModel = viewModel(factory = EmployeeViewModel.Factory)
) {
    val employees by viewModel.allEmployees.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var filterNoVehicle by remember { mutableStateOf(false) }
    var filterExpiring by remember { mutableStateOf(false) }
    
    val filteredEmployees = employees.filter { 
        val matchesSearch = it.dniPasaporte.contains(searchQuery, ignoreCase = true) || it.nombre.contains(searchQuery, ignoreCase = true)
        
        val matchesNoVehicle = if (filterNoVehicle) it.assignedVehicleId == null else true
        
        val matchesExpiring = if (filterExpiring) {
            val now = System.currentTimeMillis()
            val thirtyDays = 30L * 24 * 60 * 60 * 1000
            (it.caducidadCarnet - now < thirtyDays)
        } else true

        matchesSearch && matchesNoVehicle && matchesExpiring
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Empleado")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar por DNI o Nombre") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )
            
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = filterNoVehicle,
                    onClick = { filterNoVehicle = !filterNoVehicle },
                    label = { Text("Sin Vehículo") }
                )
                FilterChip(
                    selected = filterExpiring,
                    onClick = { filterExpiring = !filterExpiring },
                    label = { Text("Carnet Vence Pronto") }
                )
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredEmployees) { employee ->
                    EmployeeCard(employee = employee, onClick = { onNavigateToEdit(employee.id) })
                }
            }
        }
    }
}

@Composable
fun EmployeeCard(employee: Employee, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "${employee.nombre} ${employee.apellido}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "DNI: ${employee.dniPasaporte}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Licencia vence: ${formatDate(employee.caducidadCarnet)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeEntryScreen(
    employeeId: Int = 0,
    onNavigateBack: () -> Unit,
    onNavigateToAssignVehicle: (Int) -> Unit, // Pass ID to know who to assign
    viewModel: EmployeeViewModel = viewModel(factory = EmployeeViewModel.Factory)
) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var dob by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var dni by remember { mutableStateOf("") }
    var licenseType by remember { mutableStateOf("") }
    var licenseExpiry by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var assignedVehicleId by remember { mutableStateOf<Int?>(null) }
    var status by remember { mutableStateOf(EmployeeStatus.SALIR) }

    // Load logic simplified
    if (employeeId != 0) {
        val empList by viewModel.allEmployees.collectAsState()
        val emp = empList.find { it.id == employeeId }
        LaunchedEffect(emp) {
            emp?.let {
                name = it.nombre
                surname = it.apellido
                dob = it.fechaNacimiento
                dni = it.dniPasaporte
                licenseType = it.tipoCarnet
                licenseExpiry = it.caducidadCarnet
                assignedVehicleId = it.assignedVehicleId
                status = it.status
            }
        }
    }

    Scaffold { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = surname, onValueChange = { surname = it }, label = { Text("Apellido") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = dni, onValueChange = { dni = it }, label = { Text("DNI/Pasaporte") }, modifier = Modifier.fillMaxWidth())
            
            DatePickerField("Fecha Nacimiento", dob) { dob = it }
            
            OutlinedTextField(value = licenseType, onValueChange = { licenseType = it }, label = { Text("Tipo de Carnet") }, modifier = Modifier.fillMaxWidth())
            DatePickerField("Caducidad Carnet", licenseExpiry) { licenseExpiry = it }
            
            // Vehicle Assignment Section
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Vehículo asignado", style = MaterialTheme.typography.titleSmall)
                    if (assignedVehicleId == null) {
                        Text("Ninguno", style = MaterialTheme.typography.bodyMedium)
                        if (employeeId != 0) {
                            Button(onClick = { onNavigateToAssignVehicle(employeeId) }, modifier = Modifier.padding(top = 8.dp)) {
                                Text("Asignar Vehículo")
                            }
                        } else {
                            Text("Guardar empleado para asignar vehículo", style = MaterialTheme.typography.labelSmall)
                        }
                    } else {
                        // Ideally fetch vehicle info here or passing it
                        Text("ID Vehículo: $assignedVehicleId") 
                        Button(
                            onClick = { 
                                assignedVehicleId = null // Unassign locally then save
                            }, 
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Eliminar Asignación")
                        }
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.saveEmployee(
                        Employee(
                            id = employeeId,
                            nombre = name,
                            apellido = surname,
                            fechaNacimiento = dob,
                            dniPasaporte = dni,
                            tipoCarnet = licenseType,
                            caducidadCarnet = licenseExpiry,
                            assignedVehicleId = assignedVehicleId,
                            status = status
                        )
                    )
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Guardar Empleado")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignVehicleScreen(
    employeeId: Int,
    onNavigateBack: () -> Unit,
    employeeViewModel: EmployeeViewModel = viewModel(factory = EmployeeViewModel.Factory),
    vehicleViewModel: VehicleViewModel = viewModel(factory = VehicleViewModel.Factory)
) {
    var plateQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val vehicleList by vehicleViewModel.allVehicles.collectAsState()
    
    // We get the employee to update it
    val empList by employeeViewModel.allEmployees.collectAsState()
    val employee = empList.find { it.id == employeeId }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Asignar Vehículo") }) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Text("Ingrese la placa del vehículo para asignar a ${employee?.nombre ?: ""}")
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = plateQuery,
                onValueChange = { plateQuery = it },
                label = { Text("Placa del vehículo") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    // Search for vehicle
                    val vehicle = vehicleList.find { it.placa.equals(plateQuery, ignoreCase = true) }
                    if (vehicle != null) {
                        // Assign
                        if (employee != null) {
                            val updatedEmp = employee.copy(assignedVehicleId = vehicle.id)
                            employeeViewModel.saveEmployee(updatedEmp)
                            Toast.makeText(context, "Vehículo asignado con éxito", Toast.LENGTH_SHORT).show()
                            onNavigateBack()
                        }
                    } else {
                        Toast.makeText(context, "Error: Vehículo no encontrado", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Asignar")
            }
        }
    }
}
