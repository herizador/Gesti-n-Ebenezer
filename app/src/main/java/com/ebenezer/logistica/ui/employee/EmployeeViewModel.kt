package com.ebenezer.logistica.ui.employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ebenezer.logistica.EbenezerApplication
import com.ebenezer.logistica.data.model.Employee
import com.ebenezer.logistica.data.repository.EmployeeRepository
import com.ebenezer.logistica.data.repository.VehicleRepository
import com.ebenezer.logistica.logic.AlarmScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EmployeeViewModel(
    private val employeeRepository: EmployeeRepository,
    private val vehicleRepository: VehicleRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    val allEmployees: StateFlow<List<Employee>> = employeeRepository.getAllEmployees()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveEmployee(employee: Employee) {
        viewModelScope.launch {
             if (employee.id == 0) {
                 val newId = employeeRepository.insertEmployee(employee)
                 val outputEmployee = employee.copy(id = newId.toInt())
                 alarmScheduler.scheduleEmployeeLicenseAlarm(outputEmployee)
             } else {
                 employeeRepository.updateEmployee(employee)
                 alarmScheduler.scheduleEmployeeLicenseAlarm(employee)
             }
        }
    }

    fun deleteEmployee(employee: Employee) {
        viewModelScope.launch {
            alarmScheduler.cancelEmployeeAlarm(employee)
            employeeRepository.deleteEmployee(employee)
        }
    }
    
    fun assignVehicle(employeeId: Int, plate: String) {
        viewModelScope.launch {
            // Find vehicle by Plate (Need to add this method to Repo/Dao or use search)
            // Implementation detail: for now assume we can search or pass ID
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as EbenezerApplication)
                val empRepository = application.container.employeeRepository
                val vehRepository = application.container.vehicleRepository
                val alarmScheduler = application.container.alarmScheduler
                EmployeeViewModel(empRepository, vehRepository, alarmScheduler)
            }
        }
    }
}
