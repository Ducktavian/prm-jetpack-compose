package com.example.prm.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.prm.ui.persondetail.PersonDetailScreen
import com.example.prm.ui.persondetail.PersonDetailViewModel
import com.example.prm.ui.personlist.PersonListScreen
import com.example.prm.ui.personlist.PersonListViewModel

object Routes {
    const val PERSON_LIST = "person_list"
    const val PERSON_DETAIL = "person_detail/{personId}"
    fun personDetail(personId: Int) = "person_detail/$personId"
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    personListViewModel: PersonListViewModel = viewModel(),
    personDetailViewModel: PersonDetailViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.PERSON_LIST,
        modifier = modifier
    ) {
        // 1. Person List
        composable(Routes.PERSON_LIST) {
            PersonListScreen(
                viewModel = personListViewModel,
                onPersonClick = { personId ->
                    navController.navigate(Routes.personDetail(personId))
                }
            )
        }

        // 2. Person Detail
        composable(
            route = Routes.PERSON_DETAIL,
            arguments = listOf(navArgument("personId") { type = NavType.IntType })
        ) { backStackEntry ->
            val personId = backStackEntry.arguments?.getInt("personId") ?: 0
            PersonDetailScreen(
                personId = personId,
                viewModel = personDetailViewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
