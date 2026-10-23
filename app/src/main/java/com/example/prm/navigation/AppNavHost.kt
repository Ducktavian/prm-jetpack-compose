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
import com.example.prm.ui.personedit.PersonEditScreen
import com.example.prm.ui.personedit.PersonEditViewModel
import com.example.prm.ui.personlist.PersonListScreen
import com.example.prm.ui.personlist.PersonListViewModel

object Routes {
    const val PERSON_ID = "personId"
    const val PERSON_LIST = "person_list"
    const val PERSON_DETAIL = "person_detail/{$PERSON_ID}"
    fun personDetail(personId: Int) = "person_detail/$personId"
    const val PERSON_EDIT = "person_edit/{$PERSON_ID}"
    fun personEdit(personId: Int) = "person_edit/$personId"
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.PERSON_LIST,
        modifier = modifier
    ) {
        // ViewModels are created inside each destination, so each screen gets its own
        // and it's cleared when that screen leaves the back stack.

        // 1. Person List
        composable(Routes.PERSON_LIST) {
            val personListViewModel: PersonListViewModel = viewModel()
            PersonListScreen(
                viewModel = personListViewModel,
                onPersonClick = { personId ->
                    navController.navigate(Routes.personDetail(personId))
                }
            )
        }

        // 2. Person Detail (personId reaches the ViewModel via SavedStateHandle)
        composable(
            route = Routes.PERSON_DETAIL,
            arguments = listOf(navArgument(Routes.PERSON_ID) { type = NavType.IntType })
        ) {
            val personDetailViewModel: PersonDetailViewModel = viewModel()
            PersonDetailScreen(
                viewModel = personDetailViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onEditClick = { personId ->
                    navController.navigate(Routes.personEdit(personId))
                }
            )
        }

        // 3. Person Edit (Cancel and Save both pop back to Detail, which
        // refreshes itself from the repository)
        composable(
            route = Routes.PERSON_EDIT,
            arguments = listOf(navArgument(Routes.PERSON_ID) { type = NavType.IntType })
        ) {
            val personEditViewModel: PersonEditViewModel = viewModel()
            PersonEditScreen(
                viewModel = personEditViewModel,
                onDone = {
                    navController.popBackStack()
                }
            )
        }
    }
}
