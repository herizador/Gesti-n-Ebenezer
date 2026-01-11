package com.ebenezer.logistica.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EmployeeStatus {
    SALIR,
    EN_CAMINO,
    DESCANSO,
    FINALIZADO
}

@Entity(tableName = "employees")
data class Employee(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val apellido: String,
    val fechaNacimiento: Long,
    val dniPasaporte: String, // Unique identifier used for search usually
    val tipoCarnet: String,
    val caducidadCarnet: Long,
    val assignedVehicleId: Int? = null, // Foreign Key logic (can be loose or strict)
    val status: EmployeeStatus = EmployeeStatus.SALIR
)
