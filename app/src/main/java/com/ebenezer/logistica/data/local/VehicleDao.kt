package com.ebenezer.logistica.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ebenezer.logistica.data.model.Vehicle
import com.ebenezer.logistica.data.model.VehicleState
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles")
    fun getAllVehicles(): Flow<List<Vehicle>>

    @Query("SELECT * FROM vehicles WHERE id = :id")
    suspend fun getVehicleById(id: Int): Vehicle?

    @Query("SELECT * FROM vehicles WHERE placa LIKE '%' || :query || '%'")
    fun searchVehiclesByPlate(query: String): Flow<List<Vehicle>>

    @Query("SELECT * FROM vehicles WHERE estado = :state")
    fun getVehiclesByState(state: VehicleState): Flow<List<Vehicle>>

    // Get vehicles where renewal date is before a certain timestamp (expiring soon or expired)
    @Query("SELECT * FROM vehicles WHERE fechaRenovacionPlaca <= :timestamp OR fechaRenovacionSeguro <= :timestamp")
    fun getExpiringVehicles(timestamp: Long): Flow<List<Vehicle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: Vehicle): Long

    @Update
    suspend fun updateVehicle(vehicle: Vehicle)

    @Delete
    suspend fun deleteVehicle(vehicle: Vehicle)
}
