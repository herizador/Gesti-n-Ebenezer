package com.ebenezer.logistica.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ebenezer.logistica.data.model.Employee
import com.ebenezer.logistica.data.model.EmployeeStatus
import com.ebenezer.logistica.ui.employee.EmployeeViewModel
import com.ebenezer.logistica.ui.theme.SoftBlueLight
import com.ebenezer.logistica.ui.theme.SoftGreen
import com.ebenezer.logistica.ui.theme.SoftOrange
import com.ebenezer.logistica.ui.theme.SoftRed

@Composable
fun HomeScreen(
    viewModel: EmployeeViewModel = viewModel(factory = EmployeeViewModel.Factory)
) {
    val employees by viewModel.allEmployees.collectAsState()

    val exiting = employees.filter { it.status == EmployeeStatus.SALIR }
    val onRoad = employees.filter { it.status == EmployeeStatus.EN_CAMINO }
    val resting = employees.filter { it.status == EmployeeStatus.DESCANSO }
    val finished = employees.filter { it.status == EmployeeStatus.FINALIZADO }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            StatusSection("Por Salir", SoftBlueLight, exiting)
        }
        item {
            StatusSection("En Camino", SoftGreen, onRoad)
        }
        item {
            StatusSection("Descanso", SoftOrange, resting)
        }
        item {
            StatusSection("Finalizado", SoftRed, finished)
        }
    }
}

@Composable
fun StatusSection(title: String, color: Color, employees: List<Employee>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            if (employees.isEmpty()) {
                Text(text = "Ninguno", style = MaterialTheme.typography.bodyMedium)
            } else {
                employees.forEach { emp ->
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(text = "${emp.nombre} ${emp.apellido}", fontWeight = FontWeight.SemiBold)
                            // Show vehicle if assigned
                            if (emp.assignedVehicleId != null) {
                                Text(text = "Camión ID: ${emp.assignedVehicleId}", style = MaterialTheme.typography.bodySmall)
                            } else {
                                Text(text = "Sin camión", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}
