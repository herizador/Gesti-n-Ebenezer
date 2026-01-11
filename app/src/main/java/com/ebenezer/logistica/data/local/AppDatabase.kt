package com.ebenezer.logistica.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ebenezer.logistica.data.model.Employee
import com.ebenezer.logistica.data.model.Vehicle

@Database(entities = [Vehicle::class, Employee::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun employeeDao(): EmployeeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ebenezer_database"
                )
                    .fallbackToDestructiveMigration() // For dev phase only
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
