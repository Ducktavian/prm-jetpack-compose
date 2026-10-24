package com.example.prm.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
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
import com.example.prm.ui.components.MessageSnackbar
import com.example.prm.ui.interactionform.InteractionFormScreen
import com.example.prm.ui.interactionform.InteractionFormViewModel
import kotlinx.coroutines.launch

object Routes {
    const val PERSON_ID = "personId"
    const val PERSON_LIST = "person_list"
    const val PERSON_DETAIL = "person_detail/{$PERSON_ID}"
    fun personDetail(personId: Int) = "person_detail/$personId"
    const val PERSON_EDIT = "person_edit/{$PERSON_ID}"
    fun personEdit(personId: Int) = "person_edit/$personId"
    // No personId: the edit form opens blank and saving adds a new person
    const val PERSON_ADD = "person_add"
    const val INTERACTION_ADD = "interaction_add/{$PERSON_ID}"
    fun interactionAdd(personId: Int) = "interaction_add/$personId"
    const val INTERACTION_ID = "interactionId"
    const val INTERACTION_EDIT = "interaction_edit/{$PERSON_ID}/{$INTERACTION_ID}"
    fun interactionEdit(personId: Int, interactionId: Int) =
        "interaction_edit/$personId/$interactionId"
}

// A screen stops being RESUMED as soon as it navigates or is popped, so these
// ignore a second tap that lands before the transition finishes. Without this,
// a double tap opens a screen twice, or pops past the first screen (blank app).
private fun NavHostController.navigateFrom(entry: NavBackStackEntry, route: String) {
    if (entry.lifecycle.currentState == Lifecycle.State.RESUMED) navigate(route)
}

private fun NavHostController.popFrom(entry: NavBackStackEntry) {
    if (entry.lifecycle.currentState == Lifecycle.State.RESUMED) popBackStack()
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    // App-wide snackbar: lives above all screens, so a message survives navigating
    // (e.g. delete on Detail, message shows on the List)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val showMessage: (String) -> Unit = { message ->
        scope.launch { snackbarHostState.showSnackbar(message) }
    }

    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Routes.PERSON_LIST,
            modifier = Modifier.fillMaxSize()
        ) {
            // ViewModels are created inside each destination, so each screen gets its own
            // and it's cleared when that screen leaves the back stack.

            // 1. Person List
            composable(Routes.PERSON_LIST) { entry ->
                val personListViewModel: PersonListViewModel = viewModel()
                PersonListScreen(
                    viewModel = personListViewModel,
                    onPersonClick = { personId ->
                        navController.navigateFrom(entry, Routes.personDetail(personId))
                    },
                    onAddClick = {
                        navController.navigateFrom(entry, Routes.PERSON_ADD)
                    }
                )
            }

            // 2. Person Detail (personId reaches the ViewModel via SavedStateHandle)
            composable(
                route = Routes.PERSON_DETAIL,
                arguments = listOf(navArgument(Routes.PERSON_ID) { type = NavType.IntType })
            ) { entry ->
                val personDetailViewModel: PersonDetailViewModel = viewModel()
                PersonDetailScreen(
                    viewModel = personDetailViewModel,
                    onBackClick = {
                        navController.popFrom(entry)
                    },
                    onEditClick = { personId ->
                        navController.navigateFrom(entry, Routes.personEdit(personId))
                    },
                    onAddInteractionClick = { personId ->
                        navController.navigateFrom(entry, Routes.interactionAdd(personId))
                    },
                    onEditInteractionClick = { personId, interactionId ->
                        navController.navigateFrom(entry, Routes.interactionEdit(personId, interactionId))
                    },
                    // Stays on Detail (which already updated itself); just confirm it
                    onInteractionDeleted = {
                        showMessage("Interaction deleted successfully.")
                    },
                    // Person is gone, so go back to the list (which already updated itself)
                    onDeleted = {
                        navController.popFrom(entry)
                        showMessage("Person deleted successfully.")
                    }
                )
            }

            // 3. Person Edit (Cancel and Save both pop back to Detail, which
            // refreshes itself from the repository)
            composable(
                route = Routes.PERSON_EDIT,
                arguments = listOf(navArgument(Routes.PERSON_ID) { type = NavType.IntType })
            ) { entry ->
                val personEditViewModel: PersonEditViewModel = viewModel()
                PersonEditScreen(
                    viewModel = personEditViewModel,
                    onDone = {
                        navController.popFrom(entry)
                    },
                    onSaved = {
                        navController.popFrom(entry)
                        showMessage("Changes saved successfully.")
                    }
                )
            }

            // 4. Person Add (same form as Edit, opened blank; saving returns to the List)
            composable(Routes.PERSON_ADD) { entry ->
                val personAddViewModel: PersonEditViewModel = viewModel()
                PersonEditScreen(
                    viewModel = personAddViewModel,
                    onDone = {
                        navController.popFrom(entry)
                    },
                    onSaved = {
                        navController.popFrom(entry)
                        showMessage("Person added successfully.")
                    }
                )
            }

            // 5. Add Interaction (saving returns to Detail, which shows the new interaction)
            composable(
                route = Routes.INTERACTION_ADD,
                arguments = listOf(navArgument(Routes.PERSON_ID) { type = NavType.IntType })
            ) { entry ->
                val interactionFormViewModel: InteractionFormViewModel = viewModel()
                InteractionFormScreen(
                    viewModel = interactionFormViewModel,
                    onDone = {
                        navController.popFrom(entry)
                    },
                    onSaved = {
                        navController.popFrom(entry)
                        showMessage("Interaction added successfully.")
                    }
                )
            }

            // 6. Edit Interaction (same form as Add, filled in; saving returns to Detail)
            composable(
                route = Routes.INTERACTION_EDIT,
                arguments = listOf(
                    navArgument(Routes.PERSON_ID) { type = NavType.IntType },
                    navArgument(Routes.INTERACTION_ID) { type = NavType.IntType }
                )
            ) { entry ->
                val interactionFormViewModel: InteractionFormViewModel = viewModel()
                InteractionFormScreen(
                    viewModel = interactionFormViewModel,
                    onDone = {
                        navController.popFrom(entry)
                    },
                    onSaved = {
                        navController.popFrom(entry)
                        showMessage("Interaction updated successfully.")
                    }
                )
            }
        }

        // Bottom-center, above the system navigation bar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        ) { data ->
            MessageSnackbar(message = data.visuals.message)
        }
    }
}
