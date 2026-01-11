package com.ebenezer.logistica.logic

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.ebenezer.logistica.data.model.Employee
import com.ebenezer.logistica.data.model.Vehicle

interface AlarmScheduler {
    fun scheduleVehicleRenewalAlarm(vehicle: Vehicle)
    fun scheduleEmployeeLicenseAlarm(employee: Employee)
    fun cancelVehicleAlarm(vehicle: Vehicle)
    fun cancelEmployeeAlarm(employee: Employee)
}

class AlarmSchedulerImpl(private val context: Context) : AlarmScheduler {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun scheduleVehicleRenewalAlarm(vehicle: Vehicle) {
        // Schedule alarm for Plate Renewal
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("TYPE", "VEHICLE")
            putExtra("ID", vehicle.id)
            putExtra("MESSAGE", "Renovación de placa para ${vehicle.placa}")
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            vehicle.id, // Unique ID for Vehicle Alarm (using vehicle ID might collide with employee, so we offset or use different request code range)
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Note: In real app, check for permissions (SCHEDULE_EXACT_ALARM) on Android 12+
        try {
             alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                vehicle.fechaRenovacionPlaca,
                pendingIntent
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    override fun scheduleEmployeeLicenseAlarm(employee: Employee) {
         val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("TYPE", "EMPLOYEE")
            putExtra("ID", employee.id)
            putExtra("MESSAGE", "Caducidad de carnet para ${employee.nombre}")
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            employee.id + 100000, // Offset to avoid collision with Vehicle IDs
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

         try {
             alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                employee.caducidadCarnet,
                pendingIntent
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    override fun cancelVehicleAlarm(vehicle: Vehicle) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            vehicle.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    override fun cancelEmployeeAlarm(employee: Employee) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            employee.id + 100000,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
