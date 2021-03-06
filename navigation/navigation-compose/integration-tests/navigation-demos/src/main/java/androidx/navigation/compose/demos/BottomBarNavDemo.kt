/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.navigation.compose.demos

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.samples.Dashboard
import androidx.navigation.compose.samples.Profile
import androidx.navigation.compose.samples.Screen
import androidx.navigation.compose.samples.Scrollable

@Composable
fun BottomBarNavDemo() {
    val navController = rememberNavController()

    val items = listOf(
        stringResource(R.string.profile) to Screen.Profile.route,
        stringResource(R.string.dashboard) to Screen.Dashboard.route,
        stringResource(R.string.scrollable) to Screen.Scrollable.route
    )

    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry = navController.currentBackStackEntryAsState().value
                val entryRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)
                items.forEach { (name, route) ->
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Favorite) },
                        label = { Text(name) },
                        selected = entryRoute == route,
                        onClick = {
                            navController.navigate(route) {
                                launchSingleTop = true
                                popUpTo = navController.graph.startDestination
                            }
                        }
                    )
                }
            }
        }
    ) {
        NavHost(navController, startDestination = Screen.Profile.route) {
            composable(Screen.Profile.route) { Profile(navController) }
            composable(Screen.Dashboard.route) { Dashboard(navController) }
            composable(Screen.Scrollable.route) { Scrollable(navController) }
        }
    }
}
