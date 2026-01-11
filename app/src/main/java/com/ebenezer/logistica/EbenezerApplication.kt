package com.ebenezer.logistica

import android.app.Application
import com.ebenezer.logistica.data.local.AppDatabase
import com.ebenezer.logistica.data.repository.EmployeeRepository
import com.ebenezer.logistica.data.repository.EmployeeRepositoryImpl
import com.ebenezer.logistica.data.repository.VehicleRepository
import com.ebenezer.logistica.data.repository.VehicleRepositoryImpl
import com.ebenezer.logistica.logic.AlarmScheduler
import com.ebenezer.logistica.logic.AlarmSchedulerImpl

interface AppContainer {
    val vehicleRepository: VehicleRepository
    val employeeRepository: EmployeeRepository
    val alarmScheduler: AlarmScheduler
}

class DefaultAppContainer(private val context: android.content.Context) : AppContainer {
    private val database by lazy { AppDatabase.getDatabase(context) }

    override val vehicleRepository: VehicleRepository by lazy {
        VehicleRepositoryImpl(database.vehicleDao())
    }
    
    override val employeeRepository: EmployeeRepository by lazy {
        EmployeeRepositoryImpl(database.employeeDao())
    }

    override val alarmScheduler: AlarmScheduler by lazy {
        AlarmSchedulerImpl(context)
    }
}

class EbenezerApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
