package com.ebenezer.logistica.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ebenezer.logistica.data.model.Employee
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {
    @Query("SELECT * FROM employees")
    fun getAllEmployees(): Flow<List<Employee>>

    @Query("SELECT * FROM employees WHERE id = :id")
    suspend fun getEmployeeById(id: Int): Employee?

    @Query("SELECT * FROM employees WHERE dniPasaporte LIKE '%' || :query || '%'")
    fun searchEmployeesByDni(query: String): Flow<List<Employee>>

    @Query("SELECT * FROM employees WHERE assignedVehicleId IS NULL")
    fun getEmployeesWithoutVehicle(): Flow<List<Employee>>

    @Query("SELECT * FROM employees WHERE caducidadCarnet <= :timestamp")
    fun getEmployeesWithExpiringLicense(timestamp: Long): Flow<List<Employee>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: Employee): Long

    @Update
    suspend fun updateEmployee(employee: Employee)

    @Delete
    suspend fun deleteEmployee(employee: Employee)
}
