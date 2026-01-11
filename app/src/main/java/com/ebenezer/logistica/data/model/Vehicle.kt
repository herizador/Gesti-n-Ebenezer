package com.ebenezer.logistica.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

enum class VehicleState {
    REPARACION,
    ACTIVO,
    EN_CAMINO
}

@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val placa: String,
    val nombre: String, // Brand/Name of the truck
    val fechaIngreso: Long,
    val fechaRenovacionPlaca: Long,
    val fechaRenovacionSeguro: Long,
    val estado: VehicleState
)
