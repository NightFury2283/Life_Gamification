@file:Suppress("DEPRECATION")

package com.example.life_gamification.presentation


import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.life_gamification.data.auth.AuthRepositoryImpl
import com.example.life_gamification.data.auth.FirebaseAuthSource
import com.example.life_gamification.data.local.db.AppDatabase
import com.example.life_gamification.data.local.entity.UserEntity
import com.example.life_gamification.domain.auth.LoginWithGoogleUseCase
import com.example.life_gamification.presentation.Inventory.InventoryScreen
import com.example.life_gamification.presentation.Settings.SettingsScreen
import com.example.life_gamification.presentation.Shop.ShopScreen
import com.example.life_gamification.presentation.Status.StatusScreen
import com.example.life_gamification.presentation.Status.StatusViewModel
import com.example.life_gamification.presentation.Status.StatusViewModelFactory
import com.example.life_gamification.presentation.Tasks.TasksScreen
import com.example.life_gamification.presentation.auth.AuthViewModel
import com.example.life_gamification.presentation.auth.SignInScreen
import com.example.life_gamification.presentation.nav.BottomNavBar
import com.example.life_gamification.presentation.nav.BottomNavScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun AppNavHost(appContext: Context) {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()

    //авторизация
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("461383006220-j22in4hv8iegblm3lfbn3b5rh3o6cs9s.apps.googleusercontent.com")
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(appContext, gso) }
    var userId by remember { mutableStateOf<String?>(null) }
    val authViewModel = remember {
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseSource = FirebaseAuthSource(firebaseAuth)
        val repo = AuthRepositoryImpl(firebaseSource)
        val useCase = LoginWithGoogleUseCase(repo)
        AuthViewModel(useCase)
    }

    val loginState by authViewModel.loginState.observeAsState()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { token ->
                authViewModel.loginWithGoogle(token)

                val user = UserEntity(
                    id = account.id!!,
                    name = account.displayName ?: "Без имени",
                    email = account.email
                )
                userId = account.id

                val db = AppDatabase.getDatabase(appContext)
                CoroutineScope(Dispatchers.IO).launch {
                    val db = AppDatabase.getDatabase(appContext)
                    val dao = db.userDao()
                    val existingUser = dao.getUserById(account.id!!)
                    if (existingUser == null) {
                        db.userDao().insertUser(user)//только если пользователь ещё не существует
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("GoogleSignIn", "Sign-in failed: ${e.message}")
        }
    }



    // навигация
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
                googleSignInClient.signOut()
                launcher.launch(googleSignInClient.signInIntent)
            })
        }

        //показывает нижнее меню и экран
        composable("main") {
            if (userId != null) {
                MainScreen(navController = navController, userId = userId!!, authViewModel = authViewModel)
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

        composable("sign_out") {
            LaunchedEffect(Unit) {
                auth.signOut()
                googleSignInClient.signOut().addOnCompleteListener {
                    userId = null
                    authViewModel.clearLoginState()
                    navController.navigate("sign_in") {
                        popUpTo("sign_out") { inclusive = true }
                    }
                }
            }
        }
    }

}
@Composable
fun MainScreen(
    navController: NavController,
    userId: String,
    authViewModel: AuthViewModel
) {
    val statusViewModel: StatusViewModel = viewModel(
        factory = StatusViewModelFactory(
            context = LocalContext.current,
            userId = userId
        )
    )
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
                        StatusScreen(userId = userId)
                    }
                    composable(BottomNavScreen.Tasks.route) {
                        TasksScreen(userId = userId)
                    }
                    composable(BottomNavScreen.Shop.route) {
                        ShopScreen(userId = userId, statusViewModel = statusViewModel)
                    }
                    composable(BottomNavScreen.Inventory.route) {
                        InventoryScreen(userId = userId)
                    }
                    composable(BottomNavScreen.Settings.route) {
                        SettingsScreen(navController = navController, authViewModel = authViewModel)
                    }
                }
            }

            BottomNavBar(navController = innerNavController, currentRoute = currentRoute)
        }
    }
}





