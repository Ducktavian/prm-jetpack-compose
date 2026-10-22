package com.example.prm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.prm.ui.persondetail.PersonDetailScreen
import com.example.prm.ui.persondetail.PersonDetailViewModel
import com.example.prm.ui.personlist.PersonListScreen
import com.example.prm.ui.personlist.PersonListViewModel
import com.example.prm.ui.theme.PRMTheme

object Routes {
    const val PERSON_LIST = "person_list"
    const val PERSON_DETAIL = "person_detail/{personId}"
    fun personDetail(personId: Int) = "person_detail/$personId"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PRMTheme {
                val navController = rememberNavController()
                val personListViewModel: PersonListViewModel = viewModel()
                val personDetailViewModel: PersonDetailViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = Routes.PERSON_LIST
                ) {
                    composable(Routes.PERSON_LIST) {
                        PersonListScreen(
                            viewModel = personListViewModel,
                            onPersonClick = { personId ->
                                navController.navigate(Routes.personDetail(personId))
                            }
                        )
                    }

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
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PRMTheme {
        val personListViewModel = PersonListViewModel()
        PersonListScreen(viewModel = personListViewModel)
    }
}
