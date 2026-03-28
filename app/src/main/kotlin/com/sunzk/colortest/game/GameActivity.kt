package com.sunzk.colortest.game

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sunzk.colortest.BaseActivity
import com.sunzk.colortest.game.navigation.GameNavHost

class GameActivity : BaseActivity() {

    companion object {
        private const val TAG: String = "GameActivity"
    }

    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val controller = rememberNavController()
            LaunchedEffect(controller) {
                navController = controller
                Log.d(TAG, "GameActivity#onCreate- navController=$controller")
            }
            GameNavHost(navController = controller)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return (navController?.navigateUp() == true) || super.onSupportNavigateUp()
    }
}
