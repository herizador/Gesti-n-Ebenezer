package com.ebenezer.logistica.data.repository

import com.ebenezer.logistica.data.local.EmployeeDao
import com.ebenezer.logistica.data.model.Employee
import kotlinx.coroutines.flow.Flow

interface EmployeeRepository {
    fun getAllEmployees(): Flow<List<Employee>>
    suspend fun getEmployeeById(id: Int): Employee?
    fun searchEmployeesByDni(query: String): Flow<List<Employee>>
    fun getEmployeesWithoutVehicle(): Flow<List<Employee>>
    fun getEmployeesWithExpiringLicense(timestamp: Long): Flow<List<Employee>>
    suspend fun insertEmployee(employee: Employee): Long
    suspend fun updateEmployee(employee: Employee)
    suspend fun deleteEmployee(employee: Employee)
}

class EmployeeRepositoryImpl(private val employeeDao: EmployeeDao) : EmployeeRepository {
    override fun getAllEmployees(): Flow<List<Employee>> = employeeDao.getAllEmployees()

    override suspend fun getEmployeeById(id: Int): Employee? = employeeDao.getEmployeeById(id)

    override fun searchEmployeesByDni(query: String): Flow<List<Employee>> =
        employeeDao.searchEmployeesByDni(query)

    override fun getEmployeesWithoutVehicle(): Flow<List<Employee>> =
        employeeDao.getEmployeesWithoutVehicle()

    override fun getEmployeesWithExpiringLicense(timestamp: Long): Flow<List<Employee>> =
        employeeDao.getEmployeesWithExpiringLicense(timestamp)

    override suspend fun insertEmployee(employee: Employee): Long = employeeDao.insertEmployee(employee)

    override suspend fun updateEmployee(employee: Employee) = employeeDao.updateEmployee(employee)

    override suspend fun deleteEmployee(employee: Employee) = employeeDao.deleteEmployee(employee)
}
