package com.sunzk.colortest.game.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sunzk.colortest.compose.ui.FindDiffColorScreen
import com.sunzk.colortest.game.ModeSelectScreen
import com.sunzk.colortest.game.findDiffColor.FindDiffColorViewModel
import com.sunzk.colortest.game.findSameColor.FindSameColorScreen
import com.sunzk.colortest.game.findSameColor.FindSameColorViewModel
import com.sunzk.colortest.game.intermediateColor.IntermediateColorScreen
import com.sunzk.colortest.game.intermediateColor.IntermediateColorViewModel
import com.sunzk.colortest.game.mockcolor.MockColorScreen
import com.sunzk.colortest.game.mockcolor.MockColorViewModel
import com.sunzk.colortest.game.sortColor.SortColorScreen
import com.sunzk.colortest.game.sortColor.SortColorViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GameNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = GameRoutes.MODE_SELECT,
    ) {
        composable(GameRoutes.MODE_SELECT) {
            ModeSelectScreen(navController = navController)
        }
        composable(GameRoutes.MOCK_COLOR) {
            val vm: MockColorViewModel = viewModel()
            MockColorScreen(navController = navController, viewModel = vm)
        }
        composable(GameRoutes.INTERMEDIATE_COLOR) {
            val vm: IntermediateColorViewModel = viewModel()
            IntermediateColorScreen(navController = navController, viewModel = vm)
        }
        composable(GameRoutes.FIND_DIFF_COLOR) {
            val vm: FindDiffColorViewModel = viewModel()
            val scope = rememberCoroutineScope()
            FindDiffColorScreen(
                viewModel = vm,
                onCorrectAnswer = { view ->
                    view.showResult()
                    scope.launch {
                        delay(600)
                        vm.nextRandomData()
                    }
                },
            )
        }
        composable(GameRoutes.FIND_SAME_COLOR) {
            val vm: FindSameColorViewModel = viewModel()
            FindSameColorScreen(viewModel = vm)
        }
        composable(GameRoutes.SORT_COLOR) {
            val vm: SortColorViewModel = viewModel()
            SortColorScreen(viewModel = vm)
        }
    }
}
