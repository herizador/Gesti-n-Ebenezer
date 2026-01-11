package com.ebenezer.logistica.data.repository

import com.ebenezer.logistica.data.local.VehicleDao
import com.ebenezer.logistica.data.model.Vehicle
import com.ebenezer.logistica.data.model.VehicleState
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    fun getAllVehicles(): Flow<List<Vehicle>>
    suspend fun getVehicleById(id: Int): Vehicle?
    fun searchVehiclesByPlate(query: String): Flow<List<Vehicle>>
    fun getVehiclesByState(state: VehicleState): Flow<List<Vehicle>>
    fun getExpiringVehicles(timestamp: Long): Flow<List<Vehicle>>
    suspend fun insertVehicle(vehicle: Vehicle): Long
    suspend fun updateVehicle(vehicle: Vehicle)
    suspend fun deleteVehicle(vehicle: Vehicle)
}

class VehicleRepositoryImpl(private val vehicleDao: VehicleDao) : VehicleRepository {
    override fun getAllVehicles(): Flow<List<Vehicle>> = vehicleDao.getAllVehicles()
    
    override suspend fun getVehicleById(id: Int): Vehicle? = vehicleDao.getVehicleById(id)
    
    override fun searchVehiclesByPlate(query: String): Flow<List<Vehicle>> = 
        vehicleDao.searchVehiclesByPlate(query)
        
    override fun getVehiclesByState(state: VehicleState): Flow<List<Vehicle>> =
        vehicleDao.getVehiclesByState(state)
        
    override fun getExpiringVehicles(timestamp: Long): Flow<List<Vehicle>> =
        vehicleDao.getExpiringVehicles(timestamp)
        
    override suspend fun insertVehicle(vehicle: Vehicle): Long = vehicleDao.insertVehicle(vehicle)
    
    override suspend fun updateVehicle(vehicle: Vehicle) = vehicleDao.updateVehicle(vehicle)
    
    override suspend fun deleteVehicle(vehicle: Vehicle) = vehicleDao.deleteVehicle(vehicle)
}
