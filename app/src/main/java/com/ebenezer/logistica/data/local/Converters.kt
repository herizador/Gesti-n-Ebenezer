package com.ebenezer.logistica.data.local

import androidx.room.TypeConverter
import com.ebenezer.logistica.data.model.EmployeeStatus
import com.ebenezer.logistica.data.model.VehicleState
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromVehicleState(value: String): VehicleState {
        return VehicleState.valueOf(value)
    }

    @TypeConverter
    fun vehicleStateToString(state: VehicleState): String {
        return state.name
    }

    @TypeConverter
    fun fromEmployeeStatus(value: String): EmployeeStatus {
        return EmployeeStatus.valueOf(value)
    }

    @TypeConverter
    fun employeeStatusToString(status: EmployeeStatus): String {
        return status.name
    }
}
