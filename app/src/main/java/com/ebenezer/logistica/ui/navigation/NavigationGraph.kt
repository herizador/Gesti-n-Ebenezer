package com.ebenezer.logistica.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ebenezer.logistica.ui.employee.AssignVehicleScreen
import com.ebenezer.logistica.ui.employee.EmployeeEntryScreen
import com.ebenezer.logistica.ui.employee.EmployeeListScreen
import com.ebenezer.logistica.ui.home.HomeScreen
import com.ebenezer.logistica.ui.vehicle.VehicleEntryScreen
import com.ebenezer.logistica.ui.vehicle.VehicleListScreen

@Composable
fun MainAppNavigation() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestinations.Home.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppDestinations.Home.name) {
                HomeScreen()
            }
            
            // Vehicles
            composable(AppDestinations.VehicleList.name) {
                VehicleListScreen(
                    onNavigateToAdd = { navController.navigate("${AppDestinations.VehicleEntry.name}/0") },
                    onNavigateToEdit = { id -> navController.navigate("${AppDestinations.VehicleEntry.name}/$id") }
                )
            }
            
            composable(
                "${AppDestinations.VehicleEntry.name}/{vehicleId}",
                arguments = listOf(navArgument("vehicleId") { type = NavType.IntType })
            ) { backStackEntry ->
                val vehicleId = backStackEntry.arguments?.getInt("vehicleId") ?: 0
                VehicleEntryScreen(
                    vehicleId = vehicleId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Employees
            composable(AppDestinations.EmployeeList.name) {
                EmployeeListScreen(
                    onNavigateToAdd = { navController.navigate("${AppDestinations.EmployeeEntry.name}/0") },
                    onNavigateToEdit = { id -> navController.navigate("${AppDestinations.EmployeeEntry.name}/$id") }
                )
            }
            
            composable(
                "${AppDestinations.EmployeeEntry.name}/{employeeId}",
                arguments = listOf(navArgument("employeeId") { type = NavType.IntType })
            ) { backStackEntry ->
                val employeeId = backStackEntry.arguments?.getInt("employeeId") ?: 0
                EmployeeEntryScreen(
                    employeeId = employeeId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAssignVehicle = { id -> navController.navigate("${AppDestinations.EmployeeVehicleAssign.name}/$id") }
                )
            }
            
            composable(
                "${AppDestinations.EmployeeVehicleAssign.name}/{employeeId}",
                arguments = listOf(navArgument("employeeId") { type = NavType.IntType })
            ) { backStackEntry ->
                val employeeId = backStackEntry.arguments?.getInt("employeeId") ?: 0
                AssignVehicleScreen(
                    employeeId = employeeId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        AppDestinations.VehicleList to Icons.Default.LocalShipping,
        AppDestinations.EmployeeList to Icons.Default.Person,
        AppDestinations.Home to Icons.Default.Home
    )
    
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        
        items.forEach { (destination, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = destination.title) },
                label = { Text(destination.title) },
                selected = currentRoute == destination.name,
                onClick = {
                    navController.navigate(destination.name) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
