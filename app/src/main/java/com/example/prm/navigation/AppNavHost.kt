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
import com.example.prm.ui.components.MessageSnackbar
import kotlinx.coroutines.launch

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
                    },
                    // Person is gone, so go back to the list (which already updated itself)
                    onDeleted = {
                        navController.popBackStack()
                        showMessage("Person deleted successfully.")
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
                    },
                    onSaved = {
                        navController.popBackStack()
                        showMessage("Changes saved successfully.")
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
