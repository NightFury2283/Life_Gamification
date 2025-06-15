@file:Suppress("DEPRECATION")

package com.example.life_gamification.presentation

import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.life_gamification.presentation.auth.AuthViewModel
import com.example.life_gamification.presentation.auth.SignInScreen
import com.example.life_gamification.data.auth.FirebaseAuthSource
import com.example.life_gamification.data.auth.AuthRepositoryImpl
import com.example.life_gamification.data.local.db.AppDatabase
import com.example.life_gamification.data.local.entity.UserEntity
import com.example.life_gamification.domain.auth.LoginWithGoogleUseCase
import com.example.life_gamification.presentation.Status.StatusScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text


import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.life_gamification.domain.repository.UserInventoryRepositories.UserInventoryRepository
import com.example.life_gamification.presentation.Inventory.InventoryScreen
import com.example.life_gamification.presentation.Inventory.InventoryViewModel
import com.example.life_gamification.presentation.Settings.SettingsScreen
import com.example.life_gamification.presentation.Shop.ShopScreen
import com.example.life_gamification.presentation.Tasks.TasksScreen
import com.example.life_gamification.presentation.nav.BottomNavBar
import com.example.life_gamification.presentation.nav.BottomNavScreen


@Composable
fun AppNavHost(appContext: Context) {
    val navController = rememberNavController()

    // ======= АВТОРИЗАЦИЯ =======
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("461383006220-j22in4hv8iegblm3lfbn3b5rh3o6cs9s.apps.googleusercontent.com")
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(appContext, gso) }
    var userId by remember { mutableStateOf<String?>(null) }
    val viewModel = remember {
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseSource = FirebaseAuthSource(firebaseAuth)
        val repo = AuthRepositoryImpl(firebaseSource)
        val useCase = LoginWithGoogleUseCase(repo)
        AuthViewModel(useCase)
    }

    val loginState by viewModel.loginState.observeAsState()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { token ->
                viewModel.loginWithGoogle(token)

                val user = UserEntity(
                    id = account.id!!,
                    name = account.displayName ?: "Без имени",
                    email = account.email
                )
                userId = account.id

                val db = AppDatabase.getDatabase(appContext)
                CoroutineScope(Dispatchers.IO).launch {
                    db.userDao().insertUser(user)
                }
            }
        } catch (e: Exception) {
            Log.e("GoogleSignIn", "Sign-in failed: ${e.message}")
        }
    }

    // ======= Навигация =======
    LaunchedEffect(loginState) {
        if (loginState?.isSuccess == true) {
            navController.navigate("main") {
                popUpTo("sign_in") { inclusive = true }
            }
        }
    }


    NavHost(navController = navController, startDestination = "sign_in") {
        composable("sign_in") {
            SignInScreen(onSignInClick = {
                launcher.launch(googleSignInClient.signInIntent)
            })
        }

        // Это обёртка, которая показывает нижнее меню и экран
        composable("main") {
            if (userId != null) {
                MainScreen(navController = navController, userId = userId!!)
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading...", color = Color.White)
                }
            }
        }
    }

}
@Composable
fun MainScreen(navController: NavController, userId: String) {
    val innerNavController = rememberNavController()

    val currentBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                NavHost(
                    navController = innerNavController,
                    startDestination = BottomNavScreen.Status.route
                ) {
                    composable(BottomNavScreen.Status.route) {
                        StatusScreen(navController = innerNavController, userId = userId)
                    }
                    composable(BottomNavScreen.Tasks.route) {
                        TasksScreen()
                    }
                    composable(BottomNavScreen.Shop.route) {
                        ShopScreen(userId = userId)
                    }
                    composable(BottomNavScreen.Inventory.route) {
                        InventoryScreen()
                    }
                    composable(BottomNavScreen.Settings.route) {
                        SettingsScreen()
                    }
                }
            }

            BottomNavBar(navController = innerNavController, currentRoute = currentRoute)
        }
    }
}


